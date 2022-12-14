/*
 * Copyright (c) 2018 David Boissier.
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

package com.muy.utils;

import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class DateUtils {

    public static DateFormat utcDateTime(Locale locale) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    public static DateFormat utcTime(Locale locale) {
        DateFormat format = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }
}
