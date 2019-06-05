/*
 * Copyright 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter.ostrich
package admin
package config

import com.twitter.logging.{Logger, LoggerFactory}
import com.twitter.logging.config.LoggerConfig
import com.twitter.util.Config

@deprecated("no direct replacement")
abstract class ServerConfig[T <: Service] extends Config[RuntimeEnvironment => T] {
  var loggers: List[LoggerConfig] = Nil
  protected def loggerFactories: List[LoggerFactory] = Nil
  var admin = new AdminServiceConfig()

  protected var httpServer: Option[AdminHttpService] = None

  private[config] def configureLogging(): Unit = {
    if (loggerFactories.isEmpty) {
      Logger.configure(loggers)
    } else {
      Logger.configure(loggerFactories)
    }
  }

  def apply() = { (runtime: RuntimeEnvironment) =>
    configureLogging()
    httpServer = admin()(runtime)
    val service = apply(runtime)
    ServiceTracker.register(service)
    service
  }

  def apply(runtime: RuntimeEnvironment): T
}
