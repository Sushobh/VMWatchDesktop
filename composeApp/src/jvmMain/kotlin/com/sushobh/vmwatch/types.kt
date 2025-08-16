package com.sushobh.vmwatch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.lang.reflect.Field

@Serializable
data class FLProperty(
    val name: String,
    val type: String,
    val value: String? = null,
    val isMutable: Boolean = false,
    val fieldValue : String? = null,
    val isClickToShow : Boolean = false,
    val refPath : FLReferencePath
) {
    override fun toString(): String {
        return "Property(name='$name', type='$type', value=$value, isMutable=$isMutable)"
    }
}

@Serializable
data class FLPropertyOwner(
    val name: String,
    val type: String,
    val properties: List<FLProperty>
) {
    override fun toString(): String {
        return "PropertyOwner(name='$name', type='$type', properties=$properties)"
    }
}

@Serializable
data class FLSerializeFieldResponse(val isSuccess : Boolean = false,val value : FLProperty? = null)

data class FLReflectionProperty(private val field : Field,private val owner : Any)


interface FLPropertyParser {
    fun parseProperties(owner: Any): FLPropertyOwner
    fun refresh(propertyOwner : FLPropertyOwner) : FLPropertyOwner
}

interface FLPropertyStore {
    val propertyOwners : MutableMap<String, FLPropertyOwner>
}

interface FLPropertyParserInterceptor {
    fun intercept(owner : Any,field : Field) : FLProperty?
}

interface FragLensApi {
    val viewModelIdFlow : StateFlow<List<FLViewModelId>>
    fun parseProperties(flViewModelId: FLViewModelId) : FLPropertyOwner?
}

@Serializable
data class FLViewModelId(val code : Int,val name : String)

@Serializable
data class FLParserApiResponse(val isSuccess : Boolean = false,val items : List<FLProperty> = emptyList(),val viewmodelName : String)

@Serializable
data class FLReferencePath(val viewModelCode : Int,val fieldCode : Int) {

    operator fun get(index : Int) : Int {
        if(index == 0) return viewModelCode
        if(index == 1) return fieldCode
        return -1
    }

}