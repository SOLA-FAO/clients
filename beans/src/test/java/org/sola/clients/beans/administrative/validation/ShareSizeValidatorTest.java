/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.administrative.validation;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.*;
import org.sola.clients.beans.administrative.RrrShareBean;

/**
 * Issue #288 Share Size Validator Reports false failures.
 *
 * @author solaDev
 */
public class ShareSizeValidatorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // Creates a list of shares to test the share size validator. 
    private List<RrrShareBean> createShares(int numShares, short nominator, short denominator) throws NumberFormatException {
        List<RrrShareBean> shares = new ArrayList<RrrShareBean>();
        int x = 0;
        while (x < numShares) {
            x++;
            shares.add(createShare(nominator, denominator));
        }
        return shares;
    }

    // Creates an individual share record
    private RrrShareBean createShare(short nominator, short denominator) {
        RrrShareBean share = new RrrShareBean();
        share.setNominator(nominator);
        share.setDenominator(denominator);
        return share;
    }

    @Test
    public void testShareSizeValidation_equalShares() {

        ShareSizeValidator validator = new ShareSizeValidator();
        validator.setRequiredTotalSize(1);

        System.out.println("testShareSizeValidation - 2 equal shares");
        List<RrrShareBean> shares = createShares(2, new Short("1"), new Short("2"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - 3 equal shares");
        shares = createShares(3, new Short("1"), new Short("3"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - 4 equal shares");
        shares = createShares(4, new Short("1"), new Short("4"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - 7 equal shares");
        shares = createShares(7, new Short("1"), new Short("7"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - 12 equal shares");
        shares = createShares(12, new Short("1"), new Short("12"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - 31 equal shares");
        shares = createShares(31, new Short("1"), new Short("31"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - 301 equal shares");
        shares = createShares(301, new Short("1"), new Short("301"));
        assertTrue(validator.isValid(shares, null));

        System.out.println("testShareSizeValidation - share missing");
        shares = createShares(300, new Short("1"), new Short("301"));
        assertFalse(validator.isValid(shares, null));
    }

    @Test
    public void testShareSizeValidation_unequalShares() {

        ShareSizeValidator validator = new ShareSizeValidator();
        validator.setRequiredTotalSize(1);

        System.out.println("testShareSizeValidation - unequal shares A");
        List<RrrShareBean> shares = new ArrayList<RrrShareBean>();
        shares.add(createShare(new Short("1"), new Short("3")));
        shares.add(createShare(new Short("2"), new Short("3")));
        assertTrue(validator.isValid(shares, null));

        shares.clear();
        System.out.println("testShareSizeValidation - unequal shares B");
        shares.add(createShare(new Short("1"), new Short("7")));
        shares.add(createShare(new Short("2"), new Short("7")));
        shares.add(createShare(new Short("4"), new Short("7")));
        assertTrue(validator.isValid(shares, null));

        shares.clear();
        System.out.println("testShareSizeValidation - unequal shares C");
        shares.add(createShare(new Short("2"), new Short("3")));
        shares.add(createShare(new Short("1"), new Short("27")));
        shares.add(createShare(new Short("1"), new Short("27")));
        shares.add(createShare(new Short("1"), new Short("27")));
        shares.add(createShare(new Short("2"), new Short("9")));
        assertTrue(validator.isValid(shares, null));

        shares.clear();
        // Test if the shares add to more than 1
        System.out.println("testShareSizeValidation - unequal shares D");
        shares.add(createShare(new Short("2"), new Short("3")));
        shares.add(createShare(new Short("1"), new Short("27")));
        shares.add(createShare(new Short("1"), new Short("27")));
        shares.add(createShare(new Short("1"), new Short("27")));
        shares.add(createShare(new Short("1"), new Short("9")));
        shares.add(createShare(new Short("2"), new Short("27")));
        shares.add(createShare(new Short("1"), new Short("18")));
        assertFalse(validator.isValid(shares, null));

        shares.clear();
        // Test if the shares add to 1
        System.out.println("testShareSizeValidation - unequal shares E");
        shares.add(createShare(new Short("34"), new Short("100")));
        shares.add(createShare(new Short("8"), new Short("100")));
        shares.add(createShare(new Short("8"), new Short("100")));
        shares.add(createShare(new Short("8"), new Short("100")));
        shares.add(createShare(new Short("8"), new Short("100")));
        shares.add(createShare(new Short("6"), new Short("100")));
        shares.add(createShare(new Short("14"), new Short("100")));
        shares.add(createShare(new Short("14"), new Short("100")));
        assertTrue(validator.isValid(shares, null));
    }
}