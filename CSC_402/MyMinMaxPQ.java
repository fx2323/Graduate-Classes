// Exercise 2.4.29
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyMinMaxPQ<Key extends Comparable<Key>> implements Iterable<Key> {
    private Key[] a; // minheap
    private Key[] b; // maxheap
    private int[] ab; // min2max               
    private int[] ba; // max2min
    private int N; // number of items on priority queue
    private int MAXN;

    @SuppressWarnings("unchecked")
    /** Create an empty priority queue with the given initial capacity, using the given comparator. */
    public MyMinMaxPQ (int capacity) {
        MAXN = capacity;
        a = (Key[]) new Comparable[MAXN + 1];
        b = (Key[]) new Comparable[MAXN + 1];
        ab = new int[MAXN + 1];
        ba = new int[MAXN + 1];
        N = 0;
    }

    /** Is the priority queue empty? */
    public boolean isEmpty () { return N == 0; }

    /** Is the priority queue full? */
    public boolean isFull () { return N == MAXN; }

    /** Return the number of items on the priority queue. */
    public int size () { return N; }

    /**
     * Return the smallest key on the priority queue. Throw an exception if the
     * priority queue is empty.
     */
    public Key min () {
        if (isEmpty ()) throw new Error ("Priority queue underflow");
        return a[1];
    }

    /** Add a new key to the priority queue. */
    public void insert (Key x) {
        if (isFull ()) throw new Error ("Priority queue overflow");
        // add x, and percolate it up to maintain heap invariant
        N++;
        a[N] = x;
        b[N] = x;
        ab[N] = N;
        ba[N] = N;
        aSwim (N);
        bSwim (N);
        assert isMinMaxHeap ();
    }

    /**
     * Delete and return the smallest key on the priority queue. Throw an
     * exception if the priority queue is empty.
     */
    public Key delMin () {
        if (N == 0) throw new Error ("Priority queue underflow");
        Key min= a[1]; //finds min on a and sets to vairable 
        aExch(1,N); // takes min on a and sets it to N
        int removeB =ab[N]; //finds min on b
        bExch(removeB,N); // takes min on b and sets it to last
        a[N]=null; //sets min to null in a
        b[N]=null; //sets min to null in b
        N--; // reduces N by 1 since took away min from both sets
        if(N!=1){aSink(1);} //fixes a by Sinking the old a[N] only if N not equal 1
        if(b[removeB]!=null && N!=1){bSwim(removeB);} //fixes b by Swimming the old b[N] only if N not equal to 1 and removeB is not null
        assert isMinMaxHeap ();
        return min;
    }
    /**
     * Delete and return the largest key on the priority queue. Throw an
     * exception if the priority queue is empty.
     */
    public Key delMax () {
        if (N == 0) throw new Error ("Priority queue underflow");
        Key max=b[1];
        bExch(1,N);
        int removeA =ba[N];
        aExch(removeA,N);
        b[N]=null;
        a[N]=null;
        N--;
        if(N!=1){bSink(1);}
        if(a[removeA]!=null && N!=1){aSwim(removeA);}
        assert isMinMaxHeap ();
        return max;
    }

    /***********************************************************************
     * Helper functions to restore the heap invariant.
     **********************************************************************/

    private void aSwim (int k) {
        while (k > 1 && aGreater (k / 2, k)) {
            aExch (k, k / 2);
            k = k / 2;
        }
    }
    private void aSink (int k) {
        while (2 * k <= N) {
            int j = 2 * k;
            if (j < N && aGreater (j, j + 1)) j++;
            if (!aGreater (k, j)) break;
            aExch (k, j);
            k = j;
        }
    }
    private void bSwim (int k) {
        while (k > 1 && bLess (k / 2, k)) {
            bExch (k, k / 2);
            k = k / 2;
        }
    }
    private void bSink (int k) {
        while (2 * k <= N) {
            int j = 2 * k;
            if (j < N && bLess (j, j + 1)) j++;
            if (!bLess (k, j)) break;
            bExch (k, j);
            k = j;
        }
    }

    /***********************************************************************
     * Helper functions for compares and swaps.
     **********************************************************************/
    private boolean aGreater (int i, int j) {
        return a[i].compareTo (a[j]) > 0;
    }
    private boolean bLess (int i, int j) {
        return b[i].compareTo (b[j]) < 0;
    }
    private void swap (Key[] a, int i, int j) {
        Key tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
    private void swap (int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
    private void aExch (int ai, int aj) {
        int bi = ab[ai];
        int bj = ab[aj];
        swap (a, ai, aj);
        swap (ab, ai, aj);
        swap (ba, bi, bj);
    }
    private void bExch (int bi, int bj) {
        int ai = ba[bi];
        int aj = ba[bj];
        swap (b, bi, bj);
        swap (ab, ai, aj);
        swap (ba, bi, bj);
    }

    private void showHeap () {
        StdOut.print ("a:  ");
        for (int i = 1; i <= N; i++)
            StdOut.print (a[i] + " ");
        StdOut.println ();
        StdOut.print ("b:  ");
        for (int i = 1; i <= N; i++)
            StdOut.print (b[i] + " ");
        StdOut.println ();
        /*
         * StdOut.print ("ab: "); for (int i = 1; i <= N; i++) StdOut.print
         * (ab[i] + " "); StdOut.println (); StdOut.print ("ba: "); for (int i =
         * 1; i <= N; i++) StdOut.print (ba[i] + " "); StdOut.println ();
         */
    }
    private void check () {
        for (int i = 1; i <= N; i++) {
            // a and b have the same data, mapped by ab and ba
            assert a[i] == b[ab[i]];
            assert b[i] == a[ba[i]];
            // ab and ba are inverses
            assert i == ab[ba[i]];
            assert i == ba[ab[i]];
        }
    }

    private boolean isMinMaxHeap () {
        check ();
        return isMaxHeap (1) && isMinHeap (1);
    }
    private boolean isMaxHeap (int k) {
        if (k > N) return true;
        int left = 2 * k, right = 2 * k + 1;
        if (left <= N && bLess (k, left)) return false;
        if (right <= N && bLess (k, right)) return false;
        return isMaxHeap (left) && isMaxHeap (right);
    }
    private boolean isMinHeap (int k) {
        if (k > N) return true;
        int left = 2 * k, right = 2 * k + 1;
        //StdOut.printf ("checkmin: %s %s %s\n", a[k], a[left], a[right]);
        if (left <= N && aGreater (k, left)) return false;
        if (right <= N && aGreater (k, right)) return false;
        return isMinHeap (left) && isMinHeap (right);
    }

    /***********************************************************************
     * Iterator
     **********************************************************************/

    /**
     * Return an iterator that iterates over all of the keys on the priority
     * queue in ascending order.
     * <p>
     * The iterator doesn't implement <tt>remove()</tt> since it's optional.
     */
    public Iterator<Key> iterator () { return new HeapIterator (); }
    private class HeapIterator implements Iterator<Key> {
        // create a new pq
        private MyMinMaxPQ<Key> copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator () {
            copy = new MyMinMaxPQ<Key> (size ());
            for (int i = 1; i <= N; i++)
                copy.insert (a[i]);
        }

        public boolean hasNext () { return !copy.isEmpty (); }
        public void remove () { throw new UnsupportedOperationException (); }
        public Key next () {
            if (!hasNext ()) throw new NoSuchElementException ();
            return copy.delMin ();
        }
    }

    /**
     * A test client.
     */
    public static void main (String[] args) {
        MyMinMaxPQ<String> pq = new MyMinMaxPQ<String> (100);
        StdIn.fromString ("10 20 30 40 50 + - + 05 25 35 - + - 70 80 05 + - - + ");
        while (!StdIn.isEmpty ()) {
            pq.showHeap ();
            String item = StdIn.readString ();
            if (item.equals ("-")) StdOut.println ("min: " + pq.delMin ());
            else if (item.equals ("+")) StdOut.println ("max: " + pq.delMax ());
            else pq.insert (item);
        }
        StdOut.println ("(" + pq.size () + " left on pq)");
    }

}