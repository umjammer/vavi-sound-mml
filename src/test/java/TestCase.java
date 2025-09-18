/*
 * Copyright (c) 2025 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2025-09-18 nsano initial version <br>
 */
@PropsEntity(url = "file:local.properties")
class SampleTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property
    String mml = "src/test/resources/test.mml";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    void test1() throws Exception {
        LinkedList<Integer> ll = new LinkedList<>(List.of(1, 2, 3, 4, 5));
        ListIterator<Integer> lli = ll.listIterator();
Debug.print("index " + lli.previousIndex() + ", " + lli.nextIndex());
        lli.next();

Debug.print("index " + lli.previousIndex() + ", " + lli.nextIndex());
Debug.print("hasPrevious?: " + lli.hasPrevious());
        int cur = lli.previous();
Debug.print("cur: " + cur);
        assertEquals(1, cur);
        int next = lli.hasNext() ? lli.next() : -1;
        assertEquals(1, next);
    }

    @Test
    void test2() throws Exception {
        String desc = "MPI File (*.mpi)|*.mpi|MVI File (*.mvi)|*.mvi|MZI File (*.mzi)|*.mzi";
        String[] exts = Arrays.stream(desc.split("[|; ()]")).filter(s -> s.contains("*.")).distinct().map(s -> s.replace("*.", ""))
                .toArray(String[]::new);
        System.out.println(Arrays.toString(exts));
    }

    @Test
    void test3() throws Exception {
        Path path = Path.of(mml);
    }
}
