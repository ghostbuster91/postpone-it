package io.github.ghostbuster91.postponeit

import android.content.Context

var contextProvider: () -> Context = { throw RuntimeException("Context must be provided!") }
