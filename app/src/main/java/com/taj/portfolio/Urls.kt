package com.taj.portfolio

fun resolveUrl(baseUrl: String, pathOrUrl: String): String {
    return if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
        pathOrUrl
    } else {
        baseUrl.removeSuffix("/") + "/" + pathOrUrl.removePrefix("/")
    }
}
