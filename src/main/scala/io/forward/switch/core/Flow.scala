package io.forward.switch.core

import io.forward.switch.filters.{PostFilter, PreFilter}

sealed case class Flow[T](pre: PreFilter, backend: Backend, post: PostFilter[T])
