/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author odada
 */
public class DecisionTest {

    public DecisionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of call method, of class Decision.
     */
    @Test
    public void testCall() {
        Thing t1 = new Thing();
        t1.setName("7");
        ThingCategory c1 = new ThingCategory();
        t1.setCategory(c1);
        c1.setId(1L);
        c1.setName("Number");
        c1.setDescription("Numbers 1 to 100");

        Decision instance = new Decision();
        instance.setInputs(new HashSet<>());
        instance.getInputs().add(t1);
        instance.setValue("7");
        instance.setOperator(DecisionOperator.EQ);
        DecisionBranch branchYes = new DecisionBranch("Yes", instance);
        branchYes.setInputBlock(instance);
        instance.setBranchYes(branchYes);
        DecisionBranch branchNo = new DecisionBranch("No", instance);
        branchNo.setInputBlock(instance);
        instance.setBranchNo(branchNo);

        Object expResult = t1;
        Set<Object> tempResult = (Set<Object>) instance.call();
        Object result = tempResult.toArray()[0];
        assertEquals(true, Decision.flag);
    }
}
