package com.jdxiaokang.commons.dao.plugin;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class MybatisAttachThread extends Thread implements InitializingBean {

    private String jar;

    public MybatisAttachThread() {
    }

    public MybatisAttachThread(String attachJar) {
        jar = attachJar;
    }

    public void run() {
        VirtualMachine vm = null;
        List<VirtualMachineDescriptor> vms = null;
        try {
            vms = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : vms) {
                System.out.println(vmd.displayName());
                if (vmd.displayName().contains("com.jdxiaokang.service.elasticsearch.starter.ProviderApplication")) {
                    vm = VirtualMachine.attach(vmd);

                    vm.loadAgent(jar, "cxs");
                    vm.detach();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afterPropertiesSet() throws Exception {
//        FileUtils.getFile(this.getClass().getClassLoader().getResource("").getPath()).getParentFile();
        String agentJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        new MybatisAttachThread(agentJar).start();
    }
}