/*
 * Copyright 2020-2020 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.library.compress;

public final class CompressorConstants {

  public static final int COMMAND_LENGTH_MAX_NORMAL = 0b11111; /* 31 */

  public static final int COMMAND_LENGTH_MAX_EXTENDED = 0b11_11111111; /* 1023 */

  public static final byte ENF_OF_COMPRESSED_STREAM = (byte) 0xff;
}
