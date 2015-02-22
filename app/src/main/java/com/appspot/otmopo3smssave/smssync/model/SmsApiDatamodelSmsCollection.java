/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-04-15 19:10:39 UTC)
 * on 2014-05-09 at 11:35:55 UTC 
 * Modify at your own risk.
 */

package com.appspot.otmopo3smssave.smssync.model;

/**
 * Collection of Greetings.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the smssync. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SmsApiDatamodelSmsCollection extends com.google.api.client.json.GenericJson {

  /**
   * Greeting that stores a message.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<SmsApiDatamodelSms> items;

  static {
    // hack to force ProGuard to consider SmsApiDatamodelSms used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(SmsApiDatamodelSms.class);
  }

  /**
   * Greeting that stores a message.
   * @return value or {@code null} for none
   */
  public java.util.List<SmsApiDatamodelSms> getItems() {
    return items;
  }

  /**
   * Greeting that stores a message.
   * @param items items or {@code null} for none
   */
  public SmsApiDatamodelSmsCollection setItems(java.util.List<SmsApiDatamodelSms> items) {
    this.items = items;
    return this;
  }

  @Override
  public SmsApiDatamodelSmsCollection set(String fieldName, Object value) {
    return (SmsApiDatamodelSmsCollection) super.set(fieldName, value);
  }

  @Override
  public SmsApiDatamodelSmsCollection clone() {
    return (SmsApiDatamodelSmsCollection) super.clone();
  }

}