/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class LooperTest {
    
    public LooperTest() {
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
     * Test of process method, of class Looper.
     */
    @Test
    public void testProcess() {
        System.out.println("process");
        List inputs = null;
        Looper instance = new Looper("Looper1", null);
        ExecutorService service = Executors.newFixedThreadPool(20);
        service.submit(instance);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLooperType method, of class Looper.
     */
    @Test
    public void testSetLooperType() {
        System.out.println("setLooperType");
        Looper.LooperType looperType = null;
        Looper instance = new Looper("Looper1", null);
        instance.setValue(looperType);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLooperType method, of class Looper.
     */
    @Test
    public void testGetLooperType() {
        System.out.println("getLooperType");
        Looper instance = new Looper("Looper1", null);
        Looper.LooperType expResult = null;
        Looper.LooperType result = (Looper.LooperType) instance.getValue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getItems method, of class Looper.
     */
    @Test
    public void testGetItems() {
        System.out.println("getItems");
        Looper instance = new Looper("Looper1", null);
        List expResult = null;
        Collection result = instance.getInputs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setItems method, of class Looper.
     */
    @Test
    public void testSetItems() {
        System.out.println("setItems");
        Set<Object> items = new HashSet<>();
        Looper instance = new Looper("Looper1", null);
        instance.setInputs(items);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
