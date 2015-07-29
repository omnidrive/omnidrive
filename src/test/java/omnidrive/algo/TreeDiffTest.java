package omnidrive.algo;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TreeDiffTest {

    private SimpleTreeComparator comparator = new SimpleTreeComparator();

    private TreeDiff<SimpleTree, SimpleTree> diff = new TreeDiff<>(comparator);

    @Test
    public void testTwoEmptyTreesAreEqual() throws Exception {
        // Given empty trees
        SimpleTree left = new SimpleTree();
        SimpleTree right = new SimpleTree();

        // When you diff them
        TreeDiff.Result result = diff.run(left, right);

        // Then the result is equal
        assertTrue(result.areEqual());
    }

    @Test
    public void testTwoTreesWithOneItemAreEqual() throws Exception {
        // Given trees with one child with same name
        String childName = "child";
        SimpleTree left = new SimpleTree(ImmutableMap.of(childName, new SimpleTree()));
        SimpleTree right = new SimpleTree(ImmutableMap.of(childName, new SimpleTree()));

        // When you diff them
        TreeDiff.Result result = diff.run(left, right);

        // Then the result is equal
        assertTrue(result.areEqual());
    }

    @Test
    public void testLeftTreeIsEmptyAndRightTreeHasAChildNotEqual() throws Exception {
        // Given the left tree is empty
        SimpleTree left = new SimpleTree();

        // And the right tree has one child
        SimpleTree right = new SimpleTree(ImmutableMap.of("child", new SimpleTree()));

        // When you diff them
        TreeDiff.Result result = diff.run(left, right);

        // Then the result is not equal
        assertFalse(result.areEqual());
    }

    @Test
    public void testLeftTreeIsEmptyAndRightTreeHasAChildAdded() throws Exception {
        // Given the left tree is empty
        SimpleTree left = new SimpleTree();

        // And the right tree has one child
        SimpleTree child = new SimpleTree();
        SimpleTree right = new SimpleTree(ImmutableMap.of("child", child));

        // When you diff them
        TreeDiff.Result<SimpleTree, SimpleTree> result = diff.run(left, right);

        // Then the result contains the left child
        Set<SimpleTree> addedRight = result.addedRight();
        assertEquals(1, addedRight.size());
        assertTrue(addedRight.contains(child));
    }

    @Test
    public void testRightTreeIsEmptyAndLeftTreeHasAChildAdded() throws Exception {
        // Given the left tree has one child
        SimpleTree child = new SimpleTree();
        SimpleTree left = new SimpleTree(ImmutableMap.of("child", child));

        // And the right tree is empty
        SimpleTree right = new SimpleTree();

        // When you diff them
        TreeDiff.Result<SimpleTree, SimpleTree> result = diff.run(left, right);

        // Then you result contains the right child
        Set<SimpleTree> addedLeft = result.addedLeft();
        assertEquals(1, addedLeft.size());
        assertTrue(addedLeft.contains(child));
    }

    @Test
    public void testUseAComparatorToCompareNodesWithSameName() throws Exception {
        String childName = "child";

        // Given the left tree has a child
        SimpleTree leftChild = new SimpleTree(1);
        SimpleTree left = new SimpleTree(ImmutableMap.of(childName, leftChild));

        // And the right tree has a different child with same name
        SimpleTree rightChild = new SimpleTree(2);
        SimpleTree right = new SimpleTree(ImmutableMap.of(childName, rightChild));

        // When you diff them
        TreeDiff.Result<SimpleTree, SimpleTree> result = diff.run(left, right);

        // Then get the different nodes in the result
        Set<Pair<SimpleTree, SimpleTree>> modified = result.modified();
        assertEquals(1, modified.size());
        Pair<SimpleTree, SimpleTree> pair = modified.iterator().next();
        assertEquals(leftChild, pair.getLeft());
        assertEquals(rightChild, pair.getRight());
    }

    @Test
    public void testRecursiveComparison() throws Exception {
        String childName = "child";
        String grandchildName = "grandchild";

        // Given two trees with child with same name
        SimpleTree leftGrandchild = new SimpleTree(1);
        SimpleTree leftChild = new SimpleTree(ImmutableMap.of(grandchildName, leftGrandchild));
        SimpleTree left = new SimpleTree(ImmutableMap.of(childName, leftChild));

        SimpleTree rightGrandchild = new SimpleTree(2);
        SimpleTree rightChild = new SimpleTree(ImmutableMap.of(grandchildName, rightGrandchild));
        SimpleTree right = new SimpleTree(ImmutableMap.of(childName, rightChild));

        // When you diff them
        TreeDiff.Result<SimpleTree, SimpleTree> result = diff.run(left, right);

        // Then you see the changes in the subtrees
        Set<Pair<SimpleTree, SimpleTree>> modified = result.modified();
        Pair<SimpleTree, SimpleTree> pair = modified.iterator().next();
        assertEquals(leftGrandchild, pair.getLeft());
        assertEquals(rightGrandchild, pair.getRight());
    }

    private class SimpleTree implements TreeNode<SimpleTree> {

        public final int value;

        private final Map<String, SimpleTree> children;

        public SimpleTree() {
            this(Collections.<String, SimpleTree>emptyMap());
        }

        public SimpleTree(int value) {
            this(value, Collections.<String, SimpleTree>emptyMap());
        }

        public SimpleTree(Map<String, SimpleTree> children) {
            this(0, children);
        }

        public SimpleTree(int value, Map<String, SimpleTree> children) {
            this.value = value;
            this.children = children;
        }

        @Override
        public Map<String, SimpleTree> getChildren() {
            return children;
        }

    }

    private class SimpleTreeComparator implements Comparator<SimpleTree, SimpleTree> {
        @Override
        public boolean areEqual(SimpleTree left, SimpleTree right) {
            return left.value == right.value;
        }
    }
}