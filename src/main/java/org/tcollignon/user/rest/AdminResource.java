package org.tcollignon.user.rest;

import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    private static final Logger LOG = Logger.getLogger(AdminResource.class);

    @GET
    @Produces("text/plain")
    @RolesAllowed({"admin"})
    public Response monitorJavaProcess(@Context SecurityContext securityContext) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        String finalCommand = "jps -v";
        AtomicReference<String> result = runCommand(finalCommand);
        result.accumulateAndGet(System.lineSeparator() + System.lineSeparator() + "Pour exécuter le rapport de performance : /admin/procPerf?pid=XXX", (s, s2) -> s + System.lineSeparator() + s2);
        return Response.status(200).entity(result.get()).build();
    }

    @GET
    @Produces("text/plain")
    @RolesAllowed({"admin"})
    @Path("/procPerf")
    public Response executePerfCounter(@Context SecurityContext securityContext, @QueryParam("pid") String pid) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Pattern pattern = Pattern.compile("^[0-9]\\d*$");
        Matcher matcher = pattern.matcher(pid);
        if (!matcher.matches()) {
            return Response.status(403).build();
        }
        String finalCommand = "jcmd " + pid + " PerfCounter.print";
        AtomicReference<String> result = runCommand(finalCommand);
        return Response.status(200).entity(result.get()).build();
    }

    private AtomicReference<String> runCommand(String command) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File("."));
        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("/bin/sh", "-c", command);
        }
        Process process = builder.start();
        AtomicReference<String> result = new AtomicReference<>("");
        result.set("Command : " + command);
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), str -> result.accumulateAndGet(str, (s, s2) -> s + System.lineSeparator() + s2));
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            future.get(10, TimeUnit.SECONDS);
            return result;
        } else {
            throw new RuntimeException("Unable to run command : " + command);
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
    }
}