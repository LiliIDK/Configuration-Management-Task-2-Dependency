package edu.example.visualizer;

import java.util.List;

/** Простой тест без JUnit: запустить через javac/java, без сетевых запросов. */
public class SmokeTest {
    public static void main(String[] args) throws Exception {
        String xml = """
                <project><modelVersion>4.0.0</modelVersion>
                  <properties><myVersion>2.0</myVersion></properties>
                  <dependencies>
                    <dependency><groupId>org.example</groupId><artifactId>child</artifactId><version>${myVersion}</version></dependency>
                    <dependency><groupId>org.example</groupId><artifactId>test-only</artifactId><version>1.0</version><scope>test</scope></dependency>
                  </dependencies>
                </project>
                """;
        List<Coordinates> deps = new PomParser().directDependencies(xml, Coordinates.parse("org.example:root:1.0"));
        if (!deps.equals(List.of(Coordinates.parse("org.example:child:2.0"))))
            throw new AssertionError("POM разбор неверен: " + deps);
        DependencyGraph graph = new DependencyGraph();
        Coordinates a = Coordinates.parse("org.demo:a:1");
        Coordinates b = Coordinates.parse("org.demo:b:1");
        graph.addEdge(a, b);
        graph.addEdge(b, a); // цикл допустим, рёбра сохраняются
        String mermaid = new MermaidVisualizer().toMermaid(graph);
        if (!mermaid.contains("N0 --> N1") || !mermaid.contains("N1 --> N0"))
            throw new AssertionError("Mermaid генерация неверна: " + mermaid);
        System.out.println("SmokeTest OK");
    }
}
