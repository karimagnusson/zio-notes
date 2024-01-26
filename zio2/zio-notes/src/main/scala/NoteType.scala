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


private sealed trait NoteType

private case class InfoNote(
  owner: String,
  text: String
) extends NoteType

private case class WarnNote(
  owner: String,
  text: String
) extends NoteType

private case class ErrorNote(
  owner: String,
  th: Throwable
) extends NoteType

private case class DebugNote(
  owner: String,
  text: String
) extends NoteType

private case class PrintNote(
  owner: String,
  obj: Any 
) extends NoteType

