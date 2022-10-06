package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        HashMap<V, E> spt = new HashMap<>();
        Map<V, Double> distTo = new HashMap<>();
        if (Objects.equals(start, end)) {
            return spt;
        }

        distTo.put(start, 0.0);
        // for (E edge : graph.outgoingEdgesFrom(start)) {
        //     distTo.put(edge.to(), Double.POSITIVE_INFINITY);
        // }
        ExtrinsicMinPQ<V> known = createMinPQ();
        known.add(start, 0.0);

        while (!known.isEmpty()) {
            V origin = known.removeMin();
            if (Objects.equals(origin, end)) {
                return spt;
            }
            for (E edge : graph.outgoingEdgesFrom(origin)) {
                if (!distTo.containsKey(edge.to())) {
                    distTo.put(edge.to(), Double.POSITIVE_INFINITY);
                }
                V neighbor = edge.to();
                double oldDist = distTo.get(neighbor);
                double newDist = distTo.get(origin) + edge.weight();
                if (newDist < oldDist) {
                    distTo.put(neighbor, newDist);
                    spt.put(neighbor, edge);
                    if (known.contains(neighbor)) {
                        known.changePriority(neighbor, newDist);
                    } else {
                        known.add(neighbor, newDist);
                    }
                }
            }
        }
        return spt;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (Objects.equals(start, end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        List<E> list = new LinkedList<>();
        E edge = spt.get(end);
        if (edge == null) {
            return new ShortestPath.Failure<>();
        }
        list.add(edge);
        while (!Objects.equals(edge.from(), start)) {
            edge = spt.get(edge.from());
            if (edge == null) {
                return new ShortestPath.Failure<>();
            }
            list.add(edge);
        }
        Collections.reverse(list);
        return new ShortestPath.Success<>(list);
    }
}
