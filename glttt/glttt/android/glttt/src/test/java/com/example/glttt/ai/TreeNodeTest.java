package com.example.glttt.ai;

import com.example.glttt.GameBoard;
import com.example.glttt.GamePresenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class TreeNodeTest {

    @Test
    public void testConstruction() {
        TreeNode n1 = new TreeNode(null);
        TreeNode n2 = new TreeNode(n1, 0, null);

        assertEquals(n1, n2.getParent());
        assertEquals(n2, n1.getChild(0));
        assertEquals(null, n1.getChild(1));
        assertEquals(null, n1.getChild(7));
        assertEquals(null, n1.getParent());
    }

    @Test
    public void testDetach() {
        TreeNode n1 = new TreeNode(null);
        TreeNode n2 = new TreeNode(n1, 0, null);

        n2.detach();

        assertEquals(null, n2.getParent());
        assertEquals(null, n1.getChild(0));
    }

    @Test
    public void testExistingChild() {
        TreeNode n1 = new TreeNode(null);
        TreeNode n2 = new TreeNode(n1, 0, null);

        try {
            TreeNode n3 = new TreeNode(n1, 0, null);
            fail("did not get expected exception!");
        } catch (RuntimeException e) {
            // success
        }
    }

    @Test
    public void testPrune() {
        TreeNode n1 = new TreeNode(null);
        TreeNode n2 = new TreeNode(n1, 0, null);
        TreeNode n3 = new TreeNode(n1, 1, null);

        n1.prune();
        assertEquals(null, n2.getParent());
        assertEquals(null, n1.getChild(0));
        assertEquals(null, n1.getChild(1));
    }

    @Test
    public void testLevel() {
        TreeNode n1 = new TreeNode(null);
        TreeNode n2 = new TreeNode(n1, 0, null);
        TreeNode n3 = new TreeNode(n2, 1, null);

        assertEquals(0, n1.level());
        assertEquals(1, n2.level());
        assertEquals(2, n3.level());
    }

    @Test
    public void testSize() {
        TreeNode n1 = new TreeNode(null);
        TreeNode n2 = new TreeNode(n1, 0, null);
        TreeNode n3 = new TreeNode(n2, 1, null);

        assertEquals(3, n1.size());

        n3.detach();
        n3 = new TreeNode(n1, 1, null);

        assertEquals(3, n1.size());
    }

    @Test
    public void testMissingChild() {
        GameBoard rootBoard = new GameBoard();
        TreeNode n1 = new TreeNode(rootBoard);

        int mc = n1.missingChild();
        assertEquals(0, mc);

        new TreeNode(n1, 0, new GameBoard());

        mc = n1.missingChild();
        assertEquals(1, mc);

        new TreeNode(n1, 1, new GameBoard());
        new TreeNode(n1, 2, new GameBoard());
        new TreeNode(n1, 3, new GameBoard());

        mc = n1.missingChild();
        assertEquals(4, mc);

        rootBoard.moveOnPeg(4, GamePresenter.PEG_SELECT_COLOUR.RED);
        rootBoard.moveOnPeg(4, GamePresenter.PEG_SELECT_COLOUR.RED);
        rootBoard.moveOnPeg(4, GamePresenter.PEG_SELECT_COLOUR.RED);

        mc = n1.missingChild();
        assertEquals(5, mc);

        new TreeNode(n1, 5, new GameBoard());
        new TreeNode(n1, 6, new GameBoard());

        mc = n1.missingChild();
        assertEquals(7, mc);

        new TreeNode(n1, 7, new GameBoard());

        mc = n1.missingChild();
        assertEquals(-1, mc);
    }

    @Test
    public void testFindIncomplete() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("*********** starting test: testFindIncomplete");
        GameBoard rootBoard = new GameBoard();
        TreeNode rootNode = new TreeNode(rootBoard, "root");

        TreeNode incompleteNode = rootNode.findIncomplete(0, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertEquals(rootNode, incompleteNode);
        assertEquals(1, rootNode.size());

        System.out.println("building node: child0");
        TreeNode c0 = new TreeNode(rootNode, 0, new GameBoard(), "child0");

        incompleteNode = rootNode.findIncomplete(0, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertEquals(rootNode, incompleteNode);

        new TreeNode(rootNode, 1, new GameBoard(), "child1");
        new TreeNode(rootNode, 2, new GameBoard(), "child2");
        new TreeNode(rootNode, 3, new GameBoard(), "child3");
        new TreeNode(rootNode, 4, new GameBoard(), "child4");
        new TreeNode(rootNode, 5, new GameBoard(), "child5");
        new TreeNode(rootNode, 6, new GameBoard(), "child6");
        new TreeNode(rootNode, 7, new GameBoard(), "child7");

        incompleteNode = rootNode.findIncomplete(0, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertEquals(null, incompleteNode);

        incompleteNode = rootNode.findIncomplete(1, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertEquals(c0, incompleteNode);

        c0.detach();
        incompleteNode = rootNode.findIncomplete(0, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertEquals(rootNode, incompleteNode);

        rootNode.makeMove(0, GamePresenter.PEG_SELECT_COLOUR.RED);
        rootNode.makeMove(0, GamePresenter.PEG_SELECT_COLOUR.RED);
        rootNode.makeMove(0, GamePresenter.PEG_SELECT_COLOUR.RED);

        incompleteNode = rootNode.findIncomplete(0, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertEquals(null, incompleteNode);

        System.out.println("*********** test finished: testFindIncomplete\n\n\n");
    }

    @Test
    public void testFindIncompleteBuildsNewNode() {
        GameBoard rootBoard = new GameBoard();
        TreeNode rootNode = new TreeNode(rootBoard, "root");

        TreeNode incompleteNode = rootNode.findIncomplete(1, 0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertNotEquals(rootNode, incompleteNode);
        assertEquals(2, rootNode.size());
    }
}