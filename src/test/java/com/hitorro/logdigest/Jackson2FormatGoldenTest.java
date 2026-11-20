package com.hitorro.logdigest;
import tools.jackson.databind.*; import tools.jackson.databind.node.*;
import com.hitorro.logdigest.*; import com.hitorro.logdigest.commands.RunAggregate;
import com.hitorro.util.basefile.fs.BaseFile; import com.hitorro.util.basefile.tools.EnvBaseFiles;
import com.hitorro.util.io.largedata.compressedstreams.*;
import java.io.*; import java.nio.file.*; import java.util.*;
/**
 * Pins what hitorro-logdigest writes and reads to what 3.0.2 did (on Jackson 2.18 and the 3.x HiTorro
 * layers): the binary log-line files written through basefile's compressed streams for every request
 * type, reading them back (including the 3.0.2 bytes themselves), and RunAggregate's command arguments
 * read from loosely typed JSON. {@code jackson2-logdigest.txt} is the output of the same code run on
 * the 3.0.2 sources (generator: ~/hitorro-j3/tools/Jackson2LogdigestGolden.java.txt in the migration workspace).
 */
class Jackson2FormatGoldenTest {
  static final Map<String, String> out = new LinkedHashMap<>();
  static void p(String k, Object v){ out.put(k, String.valueOf(v).replace("\\","\\\\").replace("\n","\\n").replace("\r","\\r")); }
  static boolean jackson(Throwable t){ return t.getClass().getName().startsWith("com.fasterxml.jackson") || t.getClass().getName().startsWith("tools.jackson"); }
  static String desc(Throwable t){ String s = (t instanceof IOException ? "IOE:" : "RTE:") + (jackson(t) ? "jackson" : t.getClass().getSimpleName());
    if (!jackson(t) && t.getCause() != null) s += "<" + (jackson(t.getCause()) ? "jackson" : t.getCause().getClass().getSimpleName()) + ">"; return s; }
  static String tryIt(java.util.concurrent.Callable<Object> c){ try { return String.valueOf(c.call()); } catch(Throwable t){ if (System.getenv("LG_TRACE") != null) t.printStackTrace(); return "!"+desc(t); } }
  static String hex(byte[] b){ StringBuilder s = new StringBuilder(); for (byte x : b) s.append(String.format("%02x", x)); return s.toString(); }
  static void set(Object o, String f, Object v) throws Exception { java.lang.reflect.Field x = LogLineBase.class.getDeclaredField(f); x.setAccessible(true); x.set(o, v); }
  static String line(NewLogLine l){ return l.getTimeStamp() + "|" + l.getIpAddress() + "|" + l.getRequestType() + "|" + l.getSiteId() + "|" + l.getBreadcrumb() + "|" + l.getPostID() + "|" + l.getHiTorroID() + "|" + l.getRequestURI() + "|" + l.getActionType() + "|" + l.getMediaType() + "|" + l.getCategory(); }

  static List<NewLogLine> lines() throws Exception {
    List<NewLogLine> l = new ArrayList<>();
    String[] uris = {"/a/b?c=d", null, "/\u00e9t\u00e9/\uD83D\uDE00", ""};
    for (int rt = 0; rt < LogLineBase.options.length; rt++) for (int k = 0; k < 2; k++) {
      NewLogLine n = new NewLogLine();
      set(n, "_timeStamp", 1700000000 + rt * 10 + k); set(n, "_ipAddress", 0x7f000001 + rt);
      n.setRequestType((byte) rt); n.setSiteId(k == 0 ? 7 : 300000); n.setBreadcrumb(k == 0 ? "home>news" : null);
      n.setPostID(k == 0 ? 42 : 1 << 20); n.setHiTorroID(9); n.setRequestURI(uris[(rt + k) % uris.length]); n.setActionType((byte) (rt + 1)); n.setMediaType((byte) 3); n.setCategory(k == 0 ? "sports" : null);
      l.add(n); }
    return l;
  }

  public static Map<String, String> compute(Path dir) throws Exception {
    out.clear();
    ObjectMapper om = com.hitorro.util.json.JsonMappers.json();
    // ---- the binary log-line files the digester writes (through basefile's compressed streams)
    BaseFile f = EnvBaseFiles.getBaseFile(dir.resolve("lines.bin").toFile());
    List<NewLogLine> ls = lines();
    p("lines.write", tryIt(() -> { COutputStream os = f.getCOutputStream(); for (NewLogLine n : ls) n.write(os); ls.get(0).close(os); os.close(); return "ok"; }));
    p("lines.bytes", tryIt(() -> hex(Files.readAllBytes(dir.resolve("lines.bin")))));
    p("lines.read", tryIt(() -> read(f)));
    // ---- command arguments (RunAggregate's properties applied to loosely typed JSON)
    String[] args = {"{\"aggregate\":\"daily\",\"year\":2010,\"month\":\"3\",\"day\":4.9,\"quarter\":true}", "{\"aggregate\":5,\"year\":\"x\"}", "{\"aggregate\":null,\"year\":null}", "{}", "{\"year\":12345678901}"};
    for (String fn : new String[]{"aggregate", "year", "month", "day", "quarter"}) for (int i = 0; i < args.length; i++) { String a = args[i];
      p("arg." + fn + "." + i, tryIt(() -> { java.lang.reflect.Field x = RunAggregate.class.getDeclaredField(fn); x.setAccessible(true);
        Object prop = x.get(new RunAggregate()); java.lang.reflect.Method m = prop.getClass().getMethod("apply", JsonNode.class); return m.invoke(prop, om.readTree(a)); })); }
    return out;
  }
  static String read(BaseFile f) throws Exception { CInputStream is = f.getCInputStream(); List<String> r = new ArrayList<>(); NewLogLine n = new NewLogLine(); while (n.read(is)) { r.add(line(n)); n = new NewLogLine(); } is.close(); return r.toString(); }
  static Map<String, String> load() throws IOException {
    Map<String, String> m = new LinkedHashMap<>();
    try (InputStream in = Jackson2FormatGoldenTest.class.getResourceAsStream("jackson2-logdigest.txt")) {
      for (String line : new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).split("\n")) {
        if (line.isEmpty()) continue;
        int i = line.indexOf('=');
        m.put(line.substring(0, i), line.substring(i + 1));
      }
    }
    return m;
  }
  static byte[] unhex(String h){ byte[] b = new byte[h.length() / 2]; for (int i = 0; i < b.length; i++) b[i] = (byte) Integer.parseInt(h.substring(2 * i, 2 * i + 2), 16); return b; }

  @org.junit.jupiter.api.io.TempDir Path tmp;

  @org.junit.jupiter.api.Test
  void matchesJackson2Output() throws Exception {
    Map<String, String> expected = load();
    org.junit.jupiter.api.Assertions.assertEquals(28, expected.size());
    Map<String, String> actual = compute(Files.createDirectories(tmp.resolve("golden")));
    org.junit.jupiter.api.Assertions.assertEquals(new ArrayList<>(expected.keySet()), new ArrayList<>(actual.keySet()));
    for (String k : expected.keySet()) org.junit.jupiter.api.Assertions.assertEquals(expected.get(k), actual.get(k), k);
  }

  /** A log-line file written by 3.0.2 (the golden bytes) reads back exactly as 3.0.2 read it. */
  @org.junit.jupiter.api.Test
  void readsLogLinesWrittenBy3x() throws Exception {
    Map<String, String> expected = load();
    Path dir = Files.createDirectories(tmp.resolve("readback"));
    Files.write(dir.resolve("old.bin"), unhex(expected.get("lines.bytes")));
    org.junit.jupiter.api.Assertions.assertEquals(expected.get("lines.read"), read(EnvBaseFiles.getBaseFile(dir.resolve("old.bin").toFile())));
  }
}
