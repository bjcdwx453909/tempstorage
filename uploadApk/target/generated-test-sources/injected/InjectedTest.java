import java.util.*;
/**
 * Entry point to auto-generated tests (generated by maven-hpi-plugin).
 * If this fails to compile, you are probably using Hudson &lt; 1.327. If so, disable
 * this code generation by configuring maven-hpi-plugin to &lt;disabledTestInjection&gt;true&lt;/disabledTestInjection&gt;.
 */
public class InjectedTest extends junit.framework.TestCase {
  public static junit.framework.Test suite() throws Exception {
    System.out.println("Running tests for "+"io.jenkins.plugins:uploadApk:1.0.5");
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("basedir","/Users/apple/Desktop/java/uploadApk");
    parameters.put("artifactId","uploadApk");
    parameters.put("packaging","hpi");
    parameters.put("outputDirectory","/Users/apple/Desktop/java/uploadApk/target/classes");
    parameters.put("testOutputDirectory","/Users/apple/Desktop/java/uploadApk/target/test-classes");
    parameters.put("requirePI","true");
    return org.jvnet.hudson.test.PluginAutomaticTestBuilder.build(parameters);
  }
}