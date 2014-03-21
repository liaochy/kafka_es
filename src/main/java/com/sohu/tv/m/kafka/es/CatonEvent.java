package com.sohu.tv.m.kafka.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created with IntelliJ IDEA.
 * User: wuzbin
 * Date: 14-3-18
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
public class CatonEvent {

    @JSONField(name = "timestamp")
    private long timestamp;
    @JSONField(name = "r_http_x_forwarded_for")
    private String remoteIp;
    @JSONField(name = "r_buffernm")
    private String buffernm;
    @JSONField(name = "r_catecode")
    private String catecode;
    @JSONField(name = "r_cdnFile")
    private String cdnFile;
    @JSONField(name = "r_cdnid")
    private String cdnid;
    @JSONField(name = "r_cdnip")
    private String cdnip;
    @JSONField(name = "r_code")
    private String code;
    @JSONField(name = "r_ct")
    private String ct;
    @JSONField(name = "r_cttime")
    private String cttime;
    @JSONField(name = "r_duFile")
    private String duFile;
    @JSONField(name = "r_duration")
    private String duration;
    @JSONField(name = "r_error")
    private String error;
    @JSONField(name = "r_httpcode")
    private String httpcode;
    @JSONField(name = "r_isp2p")
    private String isp2p;
    @JSONField(name = "r_ltype")
    private String ltype;
    @JSONField(name = "r_net")
    private String net;
    @JSONField(name = "r_os")
    private String os;
    @JSONField(name = "r_other")
    private String other;
    @JSONField(name = "r_plat")
    private String plat;
    @JSONField(name = "r_playid")
    private String playid;
    @JSONField(name = "r_playmode")
    private String playmode;
    @JSONField(name = "r_pn")
    private String pn;
    @JSONField(name = "r_poid")
    private String poid;
    @JSONField(name = "r_sid")
    private String sid;
    @JSONField(name = "r_startid")
    private String startid;
    @JSONField(name = "r_sver")
    private String sver;
    @JSONField(name = "r_sysver")
    private String sysver;
    @JSONField(name = "r_time")
    private String time;
    @JSONField(name = "r_tip")
    private String tip;
    @JSONField(name = "r_uid")
    private String uid;
    @JSONField(name = "r_version")
    private String version;
    @JSONField(name = "r_vid")
    private String vid;
    @JSONField(name = "r_vtype")
    private String vtype;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getBuffernm() {
        return buffernm;
    }

    public void setBuffernm(String buffernm) {
        this.buffernm = buffernm;
    }

    public String getCatecode() {
        return catecode;
    }

    public void setCatecode(String catecode) {
        this.catecode = catecode;
    }

    public String getCdnFile() {
        return cdnFile;
    }

    public void setCdnFile(String cdnFile) {
        this.cdnFile = cdnFile;
    }

    public String getCdnid() {
        return cdnid;
    }

    public void setCdnid(String cdnid) {
        this.cdnid = cdnid;
    }

    public String getCdnip() {
        return cdnip;
    }

    public void setCdnip(String cdnip) {
        this.cdnip = cdnip;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getCttime() {
        return cttime;
    }

    public void setCttime(String cttime) {
        this.cttime = cttime;
    }

    public String getDuFile() {
        return duFile;
    }

    public void setDuFile(String duFile) {
        this.duFile = duFile;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHttpcode() {
        return httpcode;
    }

    public void setHttpcode(String httpcode) {
        this.httpcode = httpcode;
    }

    public String getIsp2p() {
        return isp2p;
    }

    public void setIsp2p(String isp2p) {
        this.isp2p = isp2p;
    }

    public String getLtype() {
        return ltype;
    }

    public void setLtype(String ltype) {
        this.ltype = ltype;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getPlayid() {
        return playid;
    }

    public void setPlayid(String playid) {
        this.playid = playid;
    }

    public String getPlaymode() {
        return playmode;
    }

    public void setPlaymode(String playmode) {
        this.playmode = playmode;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStartid() {
        return startid;
    }

    public void setStartid(String startid) {
        this.startid = startid;
    }

    public String getSver() {
        return sver;
    }

    public void setSver(String sver) {
        this.sver = sver;
    }

    public String getSysver() {
        return sysver;
    }

    public void setSysver(String sysver) {
        this.sysver = sysver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVtype() {
        return vtype;
    }

    public void setVtype(String vtype) {
        this.vtype = vtype;
    }
}
