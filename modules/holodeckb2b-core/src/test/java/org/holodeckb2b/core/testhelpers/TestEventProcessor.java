/*
 * Copyright (C) 2016 The Holodeck B2B Team, Sander Fieten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.holodeckb2b.core.testhelpers;

import org.apache.axis2.context.MessageContext;
import org.holodeckb2b.interfaces.events.IMessageProcessingEvent;
import org.holodeckb2b.interfaces.events.IMessageProcessingEventProcessor;

import java.util.ArrayList;

/**
 * Created at 18:13 12.11.16
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class TestEventProcessor implements IMessageProcessingEventProcessor {

    public ArrayList<IMessageProcessingEvent> events = new ArrayList<>();

    @Override
    public void raiseEvent(final IMessageProcessingEvent event,
                           final MessageContext msgContext) {
        events.add(event);
    }
}
