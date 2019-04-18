package io.forward.gateway.model

import io.forward.gateway.core.backend.Backend

case class Route(method: HttpRequestMethod, path: String, backend: Backend)
