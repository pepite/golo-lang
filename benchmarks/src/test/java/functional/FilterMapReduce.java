package functional;

import clojure.lang.Var;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.Clock;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.carrotsearch.junitbenchmarks.annotation.LabelType;
import fr.citilab.gololang.benchmarks.GoloBenchmark;
import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.ScriptingContainer;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

@BenchmarkOptions(clock = Clock.NANO_TIME, warmupRounds = 10, benchmarkRounds = 10)
@BenchmarkMethodChart(filePrefix = "filter-map-reduce")
@BenchmarkHistoryChart(filePrefix = "filter-map-reduce-history", labelWith = LabelType.TIMESTAMP)
public class FilterMapReduce extends GoloBenchmark {

  private static final Class<?> GoloModule = loadGoloModule("FilterMapReduce.golo");
  private static final Class<?> GroovyClass = loadGroovyClass("FilterMapReduce.groovy");
  private static final ScriptingContainer JRubyContainer;
  private static final EmbedEvalUnit JRubyScript;
  private static final Var ClojureScript = clojureReference("filter-map-reduce.clj", "filtermapreduce)", "testcase");

  static {
    JRubyContainer = new ScriptingContainer();
    JRubyScript = jrubyEvalUnit(JRubyContainer, "filter-map-reduce.rb");
  }

  private static final List<Integer> javaList;

  static {
    javaList = new LinkedList<>();
    for (int i = 0; i < 2_000_000; i++) {
      javaList.add(i % 500);
    }
  }

  @Test
  public void golo() throws Throwable {
    Object result = GoloModule.getMethod("run", Object.class).invoke(null, javaList);
  }

  @Test
  public void groovy() throws Throwable {
    Object result = GroovyClass.getMethod("run", Object.class).invoke(null, javaList);
  }

  @Test
  public void jruby() throws Throwable {
    JRubyContainer.put("@data_set", javaList);
    Object result = JRubyScript.run();
  }

  @Test
  public void clojure() throws Throwable {
    System.out.println(ClojureScript);
    Object result = ClojureScript.invoke();
    System.out.println(result);
  }
}
