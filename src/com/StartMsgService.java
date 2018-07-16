package com;

import com.service.Server;
import com.service.core.ServerThread;
import com.tools.IniConf;

public class StartMsgService {

	public static void main(String[] args) {
		new IniConf().iniConf();
		new Thread(new ServerThread()).start();
		new Server().run();
	}
}
