package com.iydsj.sw;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.codehaus.plexus.classworlds.ClassWorld;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Maven命令行入口
 */
public class MavenCli {

    private static final String MVN_MAVEN_CONFIG = ".mvn/maven.config";

    private static final String MVN_MAVEN_HOST = "host.conf";

    public static final String MULTIMODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";

    public static void main(String[] args) throws IOException, InterruptedException {

        ClassWorld classWorld = new ClassWorld();
        MavenCli cli = new MavenCli();
        int result = cli.doMain(new CliRequest(args, classWorld));
        System.exit(result);

    }

    public MavenCli() {

    }

    public int doMain(CliRequest cliRequest) {

        //PlexusContainer localContainer = null;
        try {
            // 初始化环境变量
            initialize(cliRequest);

            cli(cliRequest);
            properties(cliRequest);
            execute(cliRequest);
        } catch (ExitException ex) {
            return ex.exitCode;
        }
        return 0;
    }

    /**
     * 初始化环境变量
     *
     * @param cliRequest
     * @throws ExitException
     */
    private void initialize(CliRequest cliRequest) throws ExitException {
        if (cliRequest.getWorkingDirectory() == null) {
            cliRequest.workingDirectory = System.getProperty("user.dir");
        }

        // mymaven主目录
        String mymavenHome = System.getProperty("mymaven.home");
        cliRequest.mymavenHome = mymavenHome;

        // 多模块项目目录
//        if(cliRequest.multiModuleProjectDirectory == null){
//            // 系统中的多模块目录
//            String basedirProperty = System.getProperty( MULTIMODULE_PROJECT_DIRECTORY );
//            if(basedirProperty == null){
//                throw new ExitException(1);
//            }
//
//            File basedir = new File(basedirProperty);
//            try{
//                cliRequest.multiModuleProjectDirectory = basedir.getCanonicalFile();
//            }catch (IOException ex){
//                cliRequest.multiModuleProjectDirectory = basedir.getAbsoluteFile();
//            }
//        }
    }

    void properties(CliRequest cliRequest) throws ExitException {
        try {
            //读取指定位置的配置文件
            File file = new File(System.getProperty("mymaven.home") + "/" + MVN_MAVEN_HOST);
            System.out.println("mymaven.home:" + file.getName());
            Properties p = new Properties();
            p.load(new FileInputStream(file));
            if (cliRequest.commandLine.hasOption("-s")) {
                String switchHost = cliRequest.commandLine.getOptionValue("s");
                if ("185".equals(switchHost)) {

                    String hostname185 = p.getProperty("hostname185", "");
                    String password185 = p.getProperty("password185", "");

                    cliRequest.hostname = hostname185;
                    cliRequest.password = password185;
                } else if ("187".equals(switchHost)) {

                    String hostname187 = p.getProperty("hostname187", "");
                    String password187 = p.getProperty("password187", "");

                    cliRequest.hostname = hostname187;
                    cliRequest.password = password187;
                }
            } else {
                throw new ExitException(3);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw new ExitException(2);
        }
    }

    void cli(CliRequest cliRequest) throws ExitException {
        CLIManager cliManager = new CLIManager();
        List<String> args = new ArrayList<String>();

        try {
            File configFile = new File(cliRequest.workingDirectory, MVN_MAVEN_CONFIG);
            if (configFile.isFile()) {
                for (String arg : Files.toString(configFile, Charsets.UTF_8).split("\\s+")) {
                    if (arg != null) {
                        args.add(arg);
                    }
                }
                CommandLine config = cliManager.parse(args.toArray(new String[args.size()]));
                cliRequest.commandLine = config;
            }

            try
            {
                args.addAll( 0, Arrays.asList( cliRequest.args ) );
                cliRequest.commandLine = cliManager.parse( args.toArray( new String[args.size()] ) );
            }
            catch ( ParseException e )
            {
                System.err.println( "Unable to parse command line options: " + e.getMessage() );
                throw new ExitException(0);
            }
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            throw new ExitException(0);
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            throw new ExitException(0);
        }
    }

    void execute(CliRequest cliRequest) throws ExitException {

        try {
            // 执行脚本apprun.sh
            String cmdstring = "sh  " + System.getProperty("mymaven.home") + "/apprun.sh";
            System.out.println("cmdString:"+cmdstring);
            Process proc = Runtime.getRuntime().exec(cmdstring);
            proc.waitFor(); //阻塞，直到上述命令执行完

            String ls_1;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((ls_1 = bufferedReader.readLine()) != null) {
                System.out.println(ls_1);
            }
            bufferedReader.close();
            proc.waitFor();

            // 执行java程序,远程执行命令

            String hostname = cliRequest.hostname;
            String password = cliRequest.password;
            Connection con = new Connection(hostname, 22);
            con.connect();
            boolean isAuthed = con.authenticateWithPassword("root", password);
            if (isAuthed) {
                Session session = null;
                session = con.openSession();
                session.execCommand("deploy");
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw new ExitException(0);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
            throw new ExitException(0);
        }

    }

    class ExitException extends Exception {
        public int exitCode;

        public ExitException(int exitCode) {
            this.exitCode = exitCode;
        }
    }
}
