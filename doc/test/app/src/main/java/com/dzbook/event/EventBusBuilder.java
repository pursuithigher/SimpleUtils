/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dzbook.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates EventBus instances with custom parameters and also allows to install a custom default EventBus instance.
 * Create a new builder using {@link EventBus#builder()}.
 */
public class EventBusBuilder {
    boolean logSubscriberExceptions = true;
    boolean logNoSubscriberMessages = true;
    boolean sendSubscriberExceptionEvent = true;
    boolean sendNoSubscriberEvent = true;
    boolean throwSubscriberException;
    boolean eventInheritance = true;
    List<Class<?>> skipMethodVerificationForClasses;

    EventBusBuilder() {
    }

    /**
     * logSubscriberExceptions
     *
     * @param logsubscriberexceptions logsubscriberexceptions
     *                                Default: true
     * @return EventBusBuilder
     */
    public EventBusBuilder logSubscriberExceptions(boolean logsubscriberexceptions) {
        this.logSubscriberExceptions = logsubscriberexceptions;
        return this;
    }

    /**
     * logNoSubscriberMessages
     *
     * @param lognosubscribermessages lognosubscribermessages
     *                                Default: true
     * @return EventBusBuilder
     */
    public EventBusBuilder logNoSubscriberMessages(boolean lognosubscribermessages) {
        this.logNoSubscriberMessages = lognosubscribermessages;
        return this;
    }

    /**
     * sendSubscriberExceptionEvent
     * Default: true
     *
     * @param event event
     * @return EventBusBuilder
     */
    public EventBusBuilder sendSubscriberExceptionEvent(boolean event) {
        this.sendSubscriberExceptionEvent = event;
        return this;
    }

    /**
     * sendNoSubscriberEvent
     * Default: true
     *
     * @param event event
     * @return EventBusBuilder
     */
    public EventBusBuilder sendNoSubscriberEvent(boolean event) {
        this.sendNoSubscriberEvent = event;
        return this;
    }

    /**
     * Fails if an subscriber throws an exception (default: false).
     * <p>
     * Tip: Use this with BuildConfig.DEBUG to let the app crash in DEBUG mode (only). This way, you won't miss
     * exceptions during development.
     *
     * @param thro thro
     * @return EventBusBuilder
     */
    public EventBusBuilder throwSubscriberException(boolean thro) {
        this.throwSubscriberException = thro;
        return this;
    }

    /**
     * By default, EventBus considers the event class hierarchy (subscribers to super classes will be notified).
     * Switching this feature off will improve posting of events. For simple event classes extending Object directly,
     * we measured a speed up of 20% for event posting. For more complex event hierarchies, the speed up should be
     * >20%.
     * <p>
     * However, keep in mind that event posting usually consumes just a small proportion of CPU time inside an app,
     * unless it is posting at high rates, e.g. hundreds/thousands of events per second.
     *
     * @param tance tance
     * @return EventBusBuilder
     */
    public EventBusBuilder eventInheritance(boolean tance) {
        this.eventInheritance = tance;
        return this;
    }


    /**
     * Provide a custom thread pool to EventBus used for async and background event delivery. This is an advanced
     * setting to that can break things: ensure the given ExecutorService won't get stuck to avoid undefined behavior.
     */

    /**
     * Method name verification is done for methods starting with onEvent to avoid typos; using this method you can
     * exclude subscriber classes from this check. Also disables checks for method modifiers (public, not static nor
     * abstract).
     *
     * @param clazz clazz
     * @return EventBusBuilder
     */
    public EventBusBuilder skipMethodVerificationFor(Class<?> clazz) {
        if (skipMethodVerificationForClasses == null) {
            skipMethodVerificationForClasses = new ArrayList<Class<?>>();
        }
        skipMethodVerificationForClasses.add(clazz);
        return this;
    }

    /**
     * Installs the default EventBus returned by {@link EventBus#getDefault()} using this builders' values. Must be
     * done only once before the first usage of the default EventBus.
     *
     * @return EventBus
     * @throws EventBusException if there's already a default EventBus instance in place
     */
    public EventBus installDefaultEventBus() {
        synchronized (EventBus.class) {
            if (EventBus.defaultInstance != null) {
                throw new EventBusException("Default instance already exists." + " It may be only set once before it's used the first time to ensure consistent behavior.");
            }
            EventBus.defaultInstance = build();
            return EventBus.defaultInstance;
        }
    }

    /**
     * Builds an EventBus based on the current configuration.
     *
     * @return EventBus
     */
    public EventBus build() {
        return new EventBus(this);
    }

}
