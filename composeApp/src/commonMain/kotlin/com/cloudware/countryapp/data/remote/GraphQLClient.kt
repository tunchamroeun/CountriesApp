package com.cloudware.countryapp.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * GraphQL client wrapper that provides enhanced error handling and interceptors for the Apollo
 * GraphQL client used throughout the data layer.
 */
class GraphQLClient(private val apolloClient: ApolloClient) {

  /**
   * Executes a GraphQL query with proper error handling and response validation.
   *
   * @param query The GraphQL query operation to execute
   * @return Result containing the response data or an error
   */
  suspend fun <D : Query.Data> executeQuery(query: Query<D>): Result<D> {
    return try {
      val response = apolloClient.query(query).execute()

      when {
        response.hasErrors() -> {
          val errorMessage =
              response.errors?.joinToString(", ") { it.message } ?: "Unknown GraphQL error"
          Result.failure(GraphQLException(errorMessage))
        }
        response.data != null -> {
          Result.success(response.data!!)
        }
        else -> {
          Result.failure(GraphQLException("No data received from server"))
        }
      }
    } catch (e: ApolloHttpException) {
      Result.failure(NetworkException("HTTP error: ${e.statusCode} - ${e.message}", e))
    } catch (e: ApolloNetworkException) {
      Result.failure(NetworkException("Network error: ${e.message}", e))
    } catch (e: ApolloException) {
      Result.failure(GraphQLException("GraphQL error: ${e.message}", e))
    } catch (e: Exception) {
      Result.failure(UnknownException("Unexpected error: ${e.message}", e))
    }
  }

  /**
   * Executes a GraphQL query and returns a Flow for reactive handling.
   *
   * @param query The GraphQL query operation to execute
   * @return Flow emitting the response data or error
   */
  fun <D : Query.Data> executeQueryAsFlow(query: Query<D>): Flow<Result<D>> {
    return apolloClient
        .query(query)
        .toFlow()
        .map { response ->
          when {
            response.hasErrors() -> {
              val errorMessage =
                  response.errors?.joinToString(", ") { it.message } ?: "Unknown GraphQL error"
              Result.failure(GraphQLException(errorMessage))
            }
            response.data != null -> {
              Result.success(response.data!!)
            }
            else -> {
              Result.failure(GraphQLException("No data received from server"))
            }
          }
        }
        .catch { e -> emit(Result.failure(mapException(e))) }
  }

  /** Maps exceptions to appropriate custom exception types. */
  private fun mapException(throwable: Throwable): Exception {
    return when (throwable) {
      is ApolloHttpException -> {
        NetworkException("HTTP error: ${throwable.statusCode} - ${throwable.message}", throwable)
      }
      is ApolloNetworkException -> {
        NetworkException("Network error: ${throwable.message}", throwable)
      }
      is ApolloException -> {
        GraphQLException("GraphQL error: ${throwable.message}", throwable)
      }
      else -> {
        UnknownException("Unexpected error: ${throwable.message}", throwable)
      }
    }
  }

  /** Cleans up resources and closes the Apollo client. */
  fun dispose() {
    apolloClient.close()
  }
}

/** Custom exception for GraphQL-specific errors. */
class GraphQLException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Custom exception for network-related errors. */
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Custom exception for unknown errors. */
class UnknownException(message: String, cause: Throwable? = null) : Exception(message, cause)
