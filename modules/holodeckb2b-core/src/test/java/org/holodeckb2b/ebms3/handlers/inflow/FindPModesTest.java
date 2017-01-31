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
package org.holodeckb2b.ebms3.handlers.inflow;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.Handler;
import org.holodeckb2b.common.mmd.xml.MessageMetaData;
import org.holodeckb2b.core.testhelpers.HolodeckB2BTestCore;
import org.holodeckb2b.ebms3.constants.MessageContextProperties;
import org.holodeckb2b.ebms3.packaging.Messaging;
import org.holodeckb2b.ebms3.packaging.SOAPEnv;
import org.holodeckb2b.ebms3.packaging.UserMessage;
import org.holodeckb2b.interfaces.core.HolodeckB2BCoreInterface;
import org.holodeckb2b.interfaces.general.EbMSConstants;
import org.holodeckb2b.pmode.helpers.PMode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created at 23:22 29.01.17
 *
 * @author Timur Shakuov (t.shakuov at gmail.com)
 */
public class FindPModesTest {

    private static String baseDir;

    private static HolodeckB2BTestCore core;

    private FindPModes handler;

    @BeforeClass
    public static void setUpClass() throws Exception {
        baseDir = FindPModesTest.class.getClassLoader()
                .getResource("handlers").getPath();
        core = new HolodeckB2BTestCore(baseDir);
        HolodeckB2BCoreInterface.setImplementation(core);
    }

    @Before
    public void setUp() throws Exception {
        handler = new FindPModes();
    }

    @Test
    public void testDoProcessing() throws Exception {
        final String mmdPath =
                this.getClass().getClassLoader()
                        .getResource("multihop/icloud/full_mmd.xml").getPath();
        final File f = new File(mmdPath);
        MessageMetaData mmd = null;
        try {
            mmd = MessageMetaData.createFromFile(f);
        } catch (final Exception e) {
            fail("Unable to test because MMD could not be read correctly!");
        }
        // Creating SOAP envelope
        SOAPEnvelope env = SOAPEnv.createEnvelope(SOAPEnv.SOAPVersion.SOAP_12);
        // Adding header
        SOAPHeaderBlock headerBlock = Messaging.createElement(env);
        // Adding UserMessage from mmd
        OMElement umElement = UserMessage.createElement(headerBlock, mmd);

        MessageContext mc = new MessageContext();
        mc.setFLOW(MessageContext.IN_FLOW);
        // Setting input message property
        mc.setProperty(MessageContextProperties.IN_USER_MESSAGE, umElement);
        try {
            mc.setEnvelope(env);
        } catch (AxisFault axisFault) {
            fail(axisFault.getMessage());
        }

        PMode pmode = new PMode();
        pmode.setMep(EbMSConstants.ONE_WAY_MEP);
        pmode.setMepBinding(EbMSConstants.ONE_WAY_PUSH);

        org.holodeckb2b.common.messagemodel.UserMessage userMessage
                = UserMessage.readElement(umElement);

        String pmodeId = userMessage.getCollaborationInfo().getAgreement().getPModeId();

        // todo this seems strange that we need to set the PMode id value separately
        // todo when it is contained within the agreement
        // todo if we don't set it the value returned by userMessage.getPModeId() is null
        userMessage.setPModeId(pmodeId);

        try {
            Handler.InvocationResponse invokeResp = handler.invoke(mc);
            assertNotNull(invokeResp);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertNotNull(mc.getProperty(MessageContextProperties.IN_USER_MESSAGE));
        //fail("Not implemented yet!");
    }
}