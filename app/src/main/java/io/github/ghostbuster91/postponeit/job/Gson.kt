package io.github.ghostbuster91.postponeit.job

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

val gsonProvider: () -> Gson = {
    val typeAdapter: TypeAdapter<DelayedJobStatus> = object : TypeAdapter<DelayedJobStatus>() {
        override fun read(input: JsonReader): DelayedJobStatus {
            input.beginObject()
            input.nextName()
            val status = when (input.nextString()) {
                DelayedJobStatus.Sent::class.java.name -> DelayedJobStatus.Sent
                DelayedJobStatus.Delivered::class.java.name -> DelayedJobStatus.Delivered
                DelayedJobStatus.Canceled::class.java.name -> DelayedJobStatus.Canceled
                DelayedJobStatus.Executed::class.java.name -> DelayedJobStatus.Executed
                DelayedJobStatus.Pending::class.java.name -> DelayedJobStatus.Pending
                DelayedJobStatus.Error::class.java.name -> {
                    input.nextName()
                    DelayedJobStatus.Error(ErrorType.valueOf(input.nextString()))
                }
                else -> throw NotImplementedError("")
            }
            input.endObject()
            return status
        }

        override fun write(out: JsonWriter, value: DelayedJobStatus) {
            out.beginObject()
            out.name("type").value(value.javaClass.name)
            if (value is DelayedJobStatus.Error) {
                out.name("value").value(value.errorType.toString())
            }
            out.endObject()
        }
    }
    GsonBuilder().registerTypeAdapter(DelayedJobStatus::class.java, typeAdapter).create()
}