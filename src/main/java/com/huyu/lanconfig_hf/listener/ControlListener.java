package com.huyu.lanconfig_hf.listener;

import com.huyu.lanconfig_hf.utils.CMDUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ___                      _
 * / __|   ___   ___   ___   | |  ___
 * | (_--- / _ \ / _ \ / _ \  | | /___)
 * \____| \___/ \___/ \___/| |_| \___
 * /
 * \___/
 * Description:
 * Date: 2018/1/20
 * Author: Huyu
 * Success is getting what you want; happiness is wanting what you get.
 */

public interface ControlListener {

	void openTTSResponse(boolean success);

	void didGetStatus(ConcurrentHashMap<CMDUtils.CmdName, Object> map);

}
