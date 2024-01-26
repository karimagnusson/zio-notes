/*
* Copyright 2021 Kári Magnússon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package io.github.karimagnusson.zio.notes

import java.io.{PrintWriter, CharArrayWriter}


private object Stacktrace {

  val wrapThrowable: Throwable => Exception = {
    case ex: Exception => ex
    case th: Throwable => new Exception(th)
  }

  def render(th: Throwable) = {
    val ex = wrapThrowable(th)
    val cw = new CharArrayWriter()
    val pw = new PrintWriter(cw)
    ex.printStackTrace(pw)
    pw.close()
    cw.toString
  }
}