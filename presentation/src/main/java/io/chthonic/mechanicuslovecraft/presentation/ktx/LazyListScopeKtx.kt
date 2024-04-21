package io.chthonic.mechanicuslovecraft.presentation.ktx

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

inline fun <T : Any> LazyListScope.items(
    items: LazyPagingItems<T>,
    crossinline itemKey: ((item: T) -> Any?) = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = { index ->
            items.peek(index)?.let { item ->
                itemKey(item)
            } ?: index
        },
    ) { index ->
        itemContent(items[index])
    }
}

inline fun <T : Any> LazyListScope.items(
    items: List<T>,
    crossinline itemKey: ((item: T) -> Any?) = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T?) -> Unit
) {
    items(
        count = items.size,
        key = { index ->
            items[index]?.let { item ->
                itemKey(item)
            } ?: index
        },
    ) { index ->
        itemContent(items[index])
    }
}