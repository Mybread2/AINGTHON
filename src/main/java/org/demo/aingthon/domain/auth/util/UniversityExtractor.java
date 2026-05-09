package org.demo.aingthon.domain.auth.util;

import java.util.Map;

public class UniversityExtractor {

    private static final Map<String, String> DOMAIN_MAP = Map.ofEntries(
            // 서울
            Map.entry("snu.ac.kr", "서울대학교"),
            Map.entry("yonsei.ac.kr", "연세대학교"),
            Map.entry("korea.ac.kr", "고려대학교"),
            Map.entry("skku.edu", "성균관대학교"),
            Map.entry("hanyang.ac.kr", "한양대학교"),
            Map.entry("sogang.ac.kr", "서강대학교"),
            Map.entry("ewha.ac.kr", "이화여자대학교"),
            Map.entry("cau.ac.kr", "중앙대학교"),
            Map.entry("khu.ac.kr", "경희대학교"),
            Map.entry("hongik.ac.kr", "홍익대학교"),
            Map.entry("dgu.ac.kr", "동국대학교"),
            Map.entry("kookmin.ac.kr", "국민대학교"),
            Map.entry("konkuk.ac.kr", "건국대학교"),
            Map.entry("sejong.ac.kr", "세종대학교"),
            Map.entry("sungshin.ac.kr", "성신여자대학교"),
            Map.entry("uos.ac.kr", "서울시립대학교"),
            Map.entry("sookmyung.ac.kr", "숙명여자대학교"),
            Map.entry("hufs.ac.kr", "한국외국어대학교"),
            Map.entry("swu.ac.kr", "서울여자대학교"),
            Map.entry("duksung.ac.kr", "덕성여자대학교"),
            Map.entry("kw.ac.kr", "광운대학교"),
            Map.entry("mju.ac.kr", "명지대학교"),
            Map.entry("smu.ac.kr", "상명대학교"),
            Map.entry("ddwu.ac.kr", "동덕여자대학교"),
            Map.entry("syu.ac.kr", "삼육대학교"),
            Map.entry("hansung.ac.kr", "한성대학교"),
            Map.entry("ssu.ac.kr", "숭실대학교"),
            Map.entry("seoultech.ac.kr", "서울과학기술대학교"),
            Map.entry("catholic.ac.kr", "가톨릭대학교"),
            Map.entry("knsu.ac.kr", "한국체육대학교"),
            Map.entry("karts.ac.kr", "한국예술종합학교"),
            Map.entry("skhu.ac.kr", "성공회대학교"),
            Map.entry("chongshin.ac.kr", "총신대학교"),
            Map.entry("knou.ac.kr", "한국방송통신대학교"),
            Map.entry("stu.ac.kr", "서울신학대학교"),
            Map.entry("skuniv.ac.kr", "서경대학교"),
            Map.entry("luther.ac.kr", "루터대학교"),
            Map.entry("bible.ac.kr", "한국성서대학교"),
            // 경기
            Map.entry("ajou.ac.kr", "아주대학교"),
            Map.entry("kyonggi.ac.kr", "경기대학교"),
            Map.entry("suwon.ac.kr", "수원대학교"),
            Map.entry("dankook.ac.kr", "단국대학교"),
            Map.entry("kangnam.ac.kr", "강남대학교"),
            Map.entry("kau.ac.kr", "한국항공대학교"),
            Map.entry("gachon.ac.kr", "가천대학교"),
            Map.entry("eulji.ac.kr", "을지대학교"),
            Map.entry("ptu.ac.kr", "평택대학교"),
            Map.entry("yongin.ac.kr", "용인대학교"),
            Map.entry("sungkyul.ac.kr", "성결대학교"),
            Map.entry("uhs.ac.kr", "협성대학교"),
            Map.entry("shinhan.ac.kr", "신한대학교"),
            Map.entry("hansei.ac.kr", "한세대학교"),
            Map.entry("daejin.ac.kr", "대진대학교"),
            Map.entry("anyang.ac.kr", "안양대학교"),
            Map.entry("tukorea.ac.kr", "한국공학대학교"),
            Map.entry("kpu.ac.kr", "한국산업기술대학교"),
            Map.entry("calvin.ac.kr", "칼빈대학교"),
            // 인천
            Map.entry("inha.ac.kr", "인하대학교"),
            Map.entry("inha.edu", "인하대학교"),
            Map.entry("inu.ac.kr", "인천대학교"),
            Map.entry("iccu.ac.kr", "인천가톨릭대학교"),
            Map.entry("ginue.ac.kr", "경인교육대학교"),
            // 해외
            Map.entry("mit.edu", "MIT"),
            Map.entry("stanford.edu", "Stanford University"),
            Map.entry("harvard.edu", "Harvard University"),
            Map.entry("berkeley.edu", "UC Berkeley")
    );

    public static String extract(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        return DOMAIN_MAP.getOrDefault(domain, domain);
    }
}
