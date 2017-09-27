package com.iydsj.sw;

import org.apache.commons.cli.CommandLine;
import org.codehaus.plexus.classworlds.ClassWorld;

import java.io.File;
import java.util.Properties;

/**
 *  命令行请求
 */
public class CliRequest {

    // 原始命令行参数
    String[] args;

    // 命令行
    CommandLine commandLine;

    // 类加载器
    ClassWorld classWorld;

    // 工作目录
    String workingDirectory;

    // 多模块项目目录
    File multiModuleProjectDirectory;

    // mymaven主目录
    String mymavenHome;

    // 用户属性
    Properties userProperties = new Properties();

    // 连接远程机器的IP地址或域名
    String hostname = null;

    // 连接远程机器的密码
    String password = null;

    public CliRequest( String[] args, ClassWorld classWorld )
    {
        this.args = args;
        this.classWorld = classWorld;
     //   this.request = new DefaultMavenExecutionRequest();
    }

    public String[] getArgs()
    {
        return args;
    }

    public CommandLine getCommandLine()
    {
        return commandLine;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public String getWorkingDirectory()
    {
        return workingDirectory;
    }


}
