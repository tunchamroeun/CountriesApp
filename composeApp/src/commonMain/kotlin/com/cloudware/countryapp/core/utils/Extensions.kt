package com.cloudware.countryapp.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** Common utility extensions for the application */

/** Wraps a Flow result in a Resource wrapper for handling loading, success, and error states */
sealed class Resource<out T> {
  object Loading : Resource<Nothing>()

  data class Success<T>(val data: T) : Resource<T>()

  data class Error(val exception: Throwable) : Resource<Nothing>()
}

/** Extension function to convert a Flow<T> to Flow<Resource<T>> */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    this.map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading) }
        .catch { emit(Resource.Error(it)) }

/** Safe cast extension function */
inline fun <reified T> Any?.safeCast(): T? = this as? T

/** String extension to check if it's a valid non-empty string */
fun String?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

/** Extension to get safe substring */
fun String.safeSubstring(startIndex: Int, endIndex: Int = length): String {
  val safeStart = startIndex.coerceAtLeast(0)
  val safeEnd = endIndex.coerceAtMost(length)
  return if (safeStart <= safeEnd) substring(safeStart, safeEnd) else ""
}

/** Capitalize first letter of each word */
fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
      word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

/** Remove extra whitespaces and trim */
fun String.normalizeWhitespace(): String = trim().replace(Regex("\\s+"), " ")

/** Check if string contains only letters */
fun String.isAlphabetic(): Boolean = all { it.isLetter() }

/** Check if string contains only digits */
fun String.isNumeric(): Boolean = all { it.isDigit() }

/** Truncate string to specified length with ellipsis */
fun String.truncate(length: Int, ellipsis: String = "..."): String =
    if (this.length <= length) this else "${take(length - ellipsis.length)}$ellipsis"

/** Convert string to URL-friendly format */
fun String.toSlug(): String =
    lowercase().replace(Regex("[^a-z0-9\\s-]"), "").replace(Regex("\\s+"), "-").trim('-')

/** Extract initials from a name */
fun String.getInitials(maxLength: Int = 2): String =
    split(" ").take(maxLength).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

/** Safe get element at index */
fun <T> List<T>.safeGet(index: Int): T? = if (index in indices) this[index] else null

/** Get random element from list */
fun <T> List<T>.randomOrNull(): T? = if (isEmpty()) null else random()

/** Partition list into chunks of specified size */
fun <T> List<T>.chunked(size: Int): List<List<T>> {
  require(size > 0) { "Chunk size must be positive" }
  return (0 until this.size step size).map { subList(it, (it + size).coerceAtMost(this.size)) }
}

/** Remove duplicates while preserving order */
fun <T> List<T>.distinctPreservingOrder(): List<T> {
  val seen = mutableSetOf<T>()
  return filter { seen.add(it) }
}

/** Find element by predicate or return default */
inline fun <T> List<T>.findOrDefault(default: T, predicate: (T) -> Boolean): T =
    find(predicate) ?: default

/** Check if list has duplicates */
fun <T> List<T>.hasDuplicates(): Boolean = size != toSet().size

/** Group consecutive elements by predicate */
inline fun <T> List<T>.groupConsecutiveBy(keySelector: (T) -> Any): List<List<T>> {
  if (isEmpty()) return emptyList()

  val result = mutableListOf<List<T>>()
  var currentGroup = mutableListOf(first())
  var currentKey = keySelector(first())

  for (i in 1 until size) {
    val element = this[i]
    val key = keySelector(element)

    if (key == currentKey) {
      currentGroup.add(element)
    } else {
      result.add(currentGroup)
      currentGroup = mutableListOf(element)
      currentKey = key
    }
  }
  result.add(currentGroup)
  return result
}

/** Format number with proper separators */
fun Long.formatWithSeparators(): String {
  return toString().reversed().chunked(3).joinToString(",").reversed()
}

/** Format population number for display */
fun Long.formatPopulation(): String =
    when {
      this >= 1_000_000_000 -> "${(this / 1_000_000_000.0 * 10).toInt() / 10.0}B"
      this >= 1_000_000 -> "${(this / 1_000_000.0 * 10).toInt() / 10.0}M"
      this >= 1_000 -> "${(this / 1_000.0 * 10).toInt() / 10.0}K"
      else -> toString()
    }

/** Convert to percentage string */
fun Double.toPercentage(decimals: Int = 1): String {
  val multiplier =
      when (decimals) {
        0 -> 1
        1 -> 10
        2 -> 100
        else -> 10 // default to 1 decimal
      }
  val rounded = ((this * 100 * multiplier).toInt()) / multiplier.toDouble()
  return "${rounded}%"
}

/** Check if collection is not null and not empty */
fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

/** Safe first element */
fun <T> Collection<T>.firstOrNull(): T? = if (isEmpty()) null else first()

/** Safe last element */
fun <T> Collection<T>.lastOrNull(): T? = if (isEmpty()) null else last()

/** Safe get value with default */
fun <K, V> Map<K, V>.getOrDefault(key: K, defaultValue: V): V = get(key) ?: defaultValue

/** Merge two maps, with values from the second map taking precedence */
fun <K, V> Map<K, V>.merge(other: Map<K, V>): Map<K, V> = this + other
