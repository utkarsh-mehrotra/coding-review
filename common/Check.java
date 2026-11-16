import java.util.*;

// Tiny test harness: no dependencies, so every template stays paste-ready.
public class Check {
    static int pass = 0, fail = 0;

    static void eq(String name, Object got, Object want) {
        if (Objects.equals(String.valueOf(got), String.valueOf(want))) pass++;
        else { fail++; System.out.println("FAIL " + name + ": got " + got + " want " + want); }
    }

    static String s(int[] a) { return Arrays.toString(a); }

    static void done(String suite) {
        System.out.println(suite + ": pass=" + pass + " fail=" + fail);
        if (fail > 0) System.exit(1);
    }
}
