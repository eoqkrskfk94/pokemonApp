package com.mj.data.repository

import com.mj.data.mapper.mapperToPokemonLocation
import com.mj.data.mapper.mapperToPokemonName
import com.mj.data.mapper.toPokemonDetail
import com.mj.data.repository.remote.PokemonRemoteDataSource
import com.mj.presentation.utils.EspressoIdlingResource
import com.mj.data.utils.dispatcherprovider.DispatcherProvider
import com.mj.domain.model.PokemonDetail
import com.mj.domain.model.PokemonLocation
import com.mj.domain.model.PokemonName
import com.mj.domain.repository.PokemonRepository
import com.mj.domain.utils.ResultOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonRemoteDataSource: PokemonRemoteDataSource,
    private val dispatcherProvider: DispatcherProvider
) : PokemonRepository {

    override suspend fun getPokemonList(): ResultOf<List<PokemonName>> = withContext(dispatcherProvider.io) {
        return@withContext try {
            // ui test 에서 background 작업 대기 신호보내기
            EspressoIdlingResource.increment()
            val result = pokemonRemoteDataSource.getPokemonList()
            // ui test 에서 background 작업 완료 신호보내기
            EspressoIdlingResource.decrement()

            if (result.isSuccessful) {
                ResultOf.Success(mapperToPokemonName(result.body()?.pokemonNames ?: listOf()))
            } else {
                ResultOf.Error(Exception(result.message()))
            }
        } catch (e: Exception) {
            ResultOf.Error(e)
        }
    }

    override suspend fun getPokemonLocations(): ResultOf<List<PokemonLocation>> = withContext(dispatcherProvider.io) {
        return@withContext try {
            EspressoIdlingResource.increment()
            val result = pokemonRemoteDataSource.getPokemonLocations()
            EspressoIdlingResource.decrement()

            if (result.isSuccessful) {
                ResultOf.Success(mapperToPokemonLocation(result.body()?.pokemonLocations ?: listOf()))
            } else {
                ResultOf.Error(Exception(result.message()))
            }
        } catch (e: Exception) {
            EspressoIdlingResource.decrement()
            ResultOf.Error(e)
        }
    }

    override suspend fun getPokemonDetail(id: String): ResultOf<PokemonDetail> = withContext(dispatcherProvider.io) {
        return@withContext try {
            EspressoIdlingResource.increment()
            val result = pokemonRemoteDataSource.getPokemonDetail(id)
            EspressoIdlingResource.decrement()

            if (result.isSuccessful) {
                ResultOf.Success(result.body()!!.toPokemonDetail())
            } else {
                ResultOf.Error(Exception(result.message()))
            }
        } catch (e: Exception) {
            EspressoIdlingResource.decrement()
            ResultOf.Error(e)
        }
    }

}