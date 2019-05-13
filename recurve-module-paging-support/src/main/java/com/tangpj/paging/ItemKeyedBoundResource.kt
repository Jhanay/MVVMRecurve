package com.tangpj.paging

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.tangpj.recurve.resource.*

abstract class ItemKeyedBoundResource<Key, ResultType, RequestType> :
        ItemKeyedBound<Key, ResultType, RequestType>(),
        RecurveBound<List<ResultType>, RequestType>,
        PageResult<ResultType>{

    private val resultPagedList = MediatorLiveData<PagedList<ResultType>>()

    override fun asListing(config: PagedList.Config): Listing<ResultType> {
        val factory
                = RecurveItemSourceFactory(this)
        val pagedList = factory.toLiveData(config)

        val pageLoadState = Transformations.switchMap(factory.sourceLiveData) {
            it.pageLoadState
        }
        return Listing(
                pagedList = pagedList,
                pageLoadState = pageLoadState,
                retry = { factory.sourceLiveData.value?.retryAllFailed() },
                refresh = { factory.sourceLiveData.value?.invalidate() })
    }

}

