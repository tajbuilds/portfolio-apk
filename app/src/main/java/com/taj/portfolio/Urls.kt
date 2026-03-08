package com.taj.portfolio

fun resolveUrl(baseUrl: String, pathOrUrl: String): String {
    return if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
        pathOrUrl
    } else {
        baseUrl.removeSuffix("/") + "/" + pathOrUrl.removePrefix("/")
    }
}

fun resolveContactFormUrl(baseUrl: String, pathOrUrl: String): String {
    if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
        return pathOrUrl
    }

    // Mobile API may return '/api/contact' as a form path, but browser route is '/contact'.
    val normalizedPath = if (pathOrUrl.startsWith("/api/")) {
        pathOrUrl.removePrefix("/api")
    } else {
        pathOrUrl
    }

    return resolveUrl(baseUrl, normalizedPath)
}
