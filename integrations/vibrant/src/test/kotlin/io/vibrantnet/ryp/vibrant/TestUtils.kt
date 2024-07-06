package io.vibrantnet.ryp.vibrant

import java.nio.file.Files
import java.nio.file.Paths

fun loadJsonFromResource(path: String): String {
    val resourceUrl = Any::class::class.java.classLoader.getResource(path)
        ?: throw IllegalArgumentException("Resource not found: $path")
    val resourcePath = Paths.get(resourceUrl.toURI())
    return Files.readString(resourcePath)
}