package lesson3

import java.util.SortedSet
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
            root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    override fun remove(element: T): Boolean {
        if (!this.contains(element)) return false
        var curNode = root ?: return false
        var prev = root ?: return false
        var rights = true
        while (curNode.value != element) {
            prev = curNode
            when {
                element > curNode.value -> {
                    curNode = curNode.right ?: return false
                    rights = true
                }
                element < curNode.value -> {
                    curNode = curNode.left ?: return false
                    rights = false
                }
            }
        }
        if (curNode.left == null && curNode.right == null) {
            when {
                curNode == root -> root = null
                rights -> prev.right = null
                else -> prev.left = null
            }
        } else if (curNode.left == null) {
            if (curNode == root) root = curNode.right
            else {
                val right = curNode.right ?: return false
                setNode(rights, prev, right)
            }
        } else if (curNode.right == null) {
            if (curNode == root) root = curNode.left
            else {
                val left = curNode.left ?: return false
                setNode(rights, prev, left)
            }
        } else {
            var min = curNode.right ?: return false
            var prevMin = curNode.right ?: return false
            while (min.left != null) {
                prevMin = min
                val left = min.left ?: return false
                min = left
            }
            when {
                curNode == root && prevMin == min -> {
                    val rootLeft = root!!.left
                    root = min
                    min.left = rootLeft
                }
                curNode == root && prevMin != min -> {
                    prevMin.left = min.right
                    root = min
                    min.left = curNode.left
                    min.right = curNode.right
                }
                prevMin == min -> setNode(rights, prev, min)
                else -> {
                    prevMin.left = min.right
                    min.right = curNode.right
                    min.left = curNode.left
                    setNode(rights, prev, min)
                }
            }
            min.left = curNode.left
        }

        size--
        return true
    }

    private fun setNode(rights: Boolean, prev: Node<T>, curNode: Node<T>) {
        if (rights) prev.right = curNode
        else prev.left = curNode

        /**
         * labor intensity: O(logN)
         * resource intensity: O(logN)
         */
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
            root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator : MutableIterator<T> {

        private var current: Node<T>? = null

        /**
         * Поиск следующего элемента
         * Средняя
         */
        private fun findNext(): Node<T>? {
            if (size == 0) throw IllegalArgumentException()
            val curNode = current ?: return find(first())
            if (curNode.value == last()) return null
            if (curNode.right != null) {
                var follow = curNode.right ?: throw IllegalArgumentException()
                while (follow.left != null)
                    follow = follow.left ?: throw IllegalArgumentException()
                return follow
            } else {
                var prev = root ?: throw IllegalArgumentException()
                var follow = root ?: throw IllegalArgumentException()
                while (prev != curNode) {
                    if (curNode.value < prev.value) {
                        follow = prev
                        prev = prev.left ?: return null
                    } else prev = prev.right ?: return null
                }
                return follow
            }
            /**
             * labor intensity: O(N*logN)
             * resource intensity: O(logN)
             */
        }

        override fun hasNext(): Boolean = findNext() != null

        override fun next(): T {
            current = findNext()
            return (current ?: throw NoSuchElementException()).value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        override fun remove() {
            TODO()
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
