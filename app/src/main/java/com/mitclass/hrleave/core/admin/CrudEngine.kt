package com.mitclass.hrleave.core.admin

import com.mitclass.hrleave.core.network.AppResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

sealed interface CrudListUiState<out T> {
    data object Loading : CrudListUiState<Nothing>
    data class Loaded<T>(val items: List<T>, val canLoadMore: Boolean, val isLoadingMore: Boolean = false) :
        CrudListUiState<T>
    data class Error(val message: String) : CrudListUiState<Nothing>
}

sealed interface CrudFormMode<out T> {
    data object Hidden : CrudFormMode<Nothing>
    data object Create : CrudFormMode<Nothing>
    data class Edit<T>(val item: T) : CrudFormMode<T>
}

/**
 * Generic paged/searchable-list + create/edit/delete engine (Task 10.1). One instance per
 * resource, constructed by that resource's thin ViewModel with its own [CrudResourceAdapter] —
 * this class and the Composables that read it never change when a new resource is added.
 */
class CrudEngine<T>(
    private val scope: CoroutineScope,
    val adapter: CrudResourceAdapter<T>,
) {
    private val _listState = MutableStateFlow<CrudListUiState<T>>(CrudListUiState.Loading)
    val listState: StateFlow<CrudListUiState<T>> = _listState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _formMode = MutableStateFlow<CrudFormMode<T>>(CrudFormMode.Hidden)
    val formMode: StateFlow<CrudFormMode<T>> = _formMode.asStateFlow()

    private val _formValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val formValues: StateFlow<Map<String, String>> = _formValues.asStateFlow()

    private val _formSaving = MutableStateFlow(false)
    val formSaving: StateFlow<Boolean> = _formSaving.asStateFlow()

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    init {
        load()
    }

    fun load() {
        scope.launch {
            _listState.value = CrudListUiState.Loading
            _listState.value = when (val result = adapter.list(0, PAGE_SIZE)) {
                is AppResult.Success -> CrudListUiState.Loaded(
                    items = result.data.first,
                    canLoadMore = result.data.first.size < result.data.second,
                )
                is AppResult.Failure -> CrudListUiState.Error(result.message)
            }
        }
    }

    fun loadMore() {
        val current = _listState.value as? CrudListUiState.Loaded ?: return
        if (!current.canLoadMore || current.isLoadingMore) return
        scope.launch {
            _listState.value = current.copy(isLoadingMore = true)
            when (val result = adapter.list(current.items.size, PAGE_SIZE)) {
                is AppResult.Success -> {
                    val combined = current.items + result.data.first
                    _listState.value = CrudListUiState.Loaded(combined, combined.size < result.data.second)
                }
                is AppResult.Failure -> _listState.value = current.copy(isLoadingMore = false)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /** Client-side filter over currently-loaded pages — none of these resources' list endpoints support server search. */
    fun visibleItems(): List<T> {
        val loaded = (_listState.value as? CrudListUiState.Loaded)?.items ?: return emptyList()
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return loaded
        return loaded.filter {
            adapter.title(it).contains(query, ignoreCase = true) || adapter.subtitle(it).contains(query, ignoreCase = true)
        }
    }

    fun startCreate() {
        _formValues.value = adapter.fields.associate { field -> field.key to if (field.type == FieldType.TOGGLE) "true" else "" }
        _formError.value = null
        _formMode.value = CrudFormMode.Create
    }

    fun startEdit(item: T) {
        _formValues.value = adapter.toFormValues(item)
        _formError.value = null
        _formMode.value = CrudFormMode.Edit(item)
    }

    fun dismissForm() {
        _formMode.value = CrudFormMode.Hidden
    }

    fun onFieldChange(key: String, value: String) {
        _formValues.value = _formValues.value + (key to value)
    }

    fun submitForm() {
        val mode = _formMode.value
        if (mode is CrudFormMode.Hidden || _formSaving.value) return
        scope.launch {
            _formSaving.value = true
            _formError.value = null
            val values = _formValues.value
            val result = when (mode) {
                is CrudFormMode.Create -> adapter.create(values)
                is CrudFormMode.Edit -> adapter.update(adapter.id(mode.item), values)
                CrudFormMode.Hidden -> return@launch
            }
            _formSaving.value = false
            when (result) {
                is AppResult.Success -> {
                    _formMode.value = CrudFormMode.Hidden
                    load()
                }
                is AppResult.Failure -> _formError.value = result.message
            }
        }
    }

    fun delete(item: T) {
        val itemId = adapter.id(item)
        if (itemId in _deletingIds.value) return
        scope.launch {
            _deletingIds.value = _deletingIds.value + itemId
            when (adapter.delete(itemId)) {
                is AppResult.Success -> load()
                is AppResult.Failure -> _deletingIds.value = _deletingIds.value - itemId
            }
        }
    }
}
