"""Original FinSync layered vector illustrations. No runtime generation/dependency."""
from pathlib import Path

OUT = Path(__file__).resolve().parents[1] / "app/src/main/res/drawable"
INK, ICE, VIOLET, AQUA = "#473D77", "#F0FCFF", "#9581E8", "#73CFCA"


def path(d, fill, stroke=None, width=1.3):
    edge = f' android:strokeColor="{stroke}" android:strokeWidth="{width}" android:strokeLineJoin="round" android:strokeLineCap="round"' if stroke else ""
    return f'<path android:pathData="{d}" android:fillColor="{fill}"{edge}/>'


def metal(d, color=VIOLET, shade=INK):
    return f'''<path android:pathData="{d}" android:strokeColor="{shade}" android:strokeWidth="0.8" android:strokeLineJoin="round">
      <aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="12" android:startY="8" android:endX="54" android:endY="60">
      <item android:offset="0" android:color="{ICE}"/><item android:offset="0.22" android:color="{color}"/>
      <item android:offset="0.48" android:color="{color}"/><item android:offset="0.8" android:color="{shade}"/>
      <item android:offset="1" android:color="{color}"/></gradient></aapt:attr></path>'''


def rect(x, y, w, h, fill, radius=3, shaded=False):
    d = f"M{x+radius},{y}h{w-radius*2}q{radius},0 {radius},{radius}v{h-radius*2}q0,{radius} -{radius},{radius}h-{w-radius*2}q-{radius},0 -{radius},-{radius}v-{h-radius*2}q0,-{radius} {radius},-{radius}z"
    return metal(d, fill) if shaded else path(d, fill)


def circle(x, y, r, fill):
    return path(f"M{x-r},{y}a{r},{r} 0,1 0,{r*2} 0a{r},{r} 0,1 0,-{r*2} 0", fill)


def line(d, color=ICE, width=1.5):
    return path(d, "#00000000", color, width)


def shadow():
    return path("M9,55C9,50 55,50 55,55C55,61 9,61 9,55", "#18352A64")


def wallet():
    return [metal("M10,17L48,10Q53,9 53,15V48L11,51z", "#B6AEDE"),
            rect(9,18,46,35,VIOLET,6,True), line("M14,22H48"), line("M14,48H46", "#BDB1F2"),
            rect(37,29,21,15,"#C1CBE5",4,True), circle(44,36,2,INK),
            line("M17,29V42", "#DCD4FF",1), circle(18,46,.8,ICE), circle(23,46,.8,ICE)]


def bank():
    return [metal("M5,23L32,8 59,23V27H5z", "#C1B5F3"), circle(32,18,3,ICE),
            rect(9,47,46,7,"#B6C5E9",2,True), rect(6,54,52,4,VIOLET,1,True),
            *[rect(x,27,7,21,"#C7D6EF",1,True)+line(f"M{x+2},29V45",ICE,1) for x in (13,28,43)]]


def credit():
    return [rect(10,10,44,27,"#85D1CC",4,True),
            rect(6,23,52,33,VIOLET,5,True), rect(7,31,50,7,INK,0),
            rect(13,42,10,7,"#F3D799",1,True), line("M16,42V49M13,45H23", "#B99B62",.8),
            circle(44,47,5,"#C8C2F0"), circle(50,47,5,"#8AD4D6"), line("M12,27H50",ICE,1)]


def cash():
    return [rect(6,25,49,30,"#61AAA4",3,True), rect(9,20,49,30,AQUA,3,True),
            path("M14,24H52V45H14z", "#B9E7DC", "#48958E"), circle(33,35,8,"#6FBAB1"),
            line("M33,30V40M30,32H35M30,38H35",ICE,1.5), circle(20,35,2,"#63A99D"),
            circle(46,35,2,"#63A99D"), rect(29,20,8,30,"#F3ECDF",0),
            line("M10,51H50", "#C6EFEC",1)]


ART = {
    "wallet": wallet(), "bank": bank(), "credit": credit(), "cash": cash(),
    "food": [circle(32,32,23,"#CFD7E5"), circle(31,30,23,"#FCFAF4"),
             circle(31,30,17,"#DDE8EC"), circle(31,30,14,"#FFFDF5"),
             metal("M23,36Q20,22 31,21Q42,21 39,35z", "#F1C093", "#B87B5B"),
             path("M22,26Q31,21 39,26L37,32H24z", "#92C89D"), circle(30,25,2,"#E89784"),
             metal("M7,13H10V25H14V13H17V25Q17,30 13,31V52H9V31Q5,30 5,25V13z", "#C3CEE9"),
             metal("M50,12Q44,18 45,32H50V53H54V12z", "#C3CEE9")],
    "shopping": [metal("M13,23L46,18 55,51Q35,60 10,52z", "#D1A3DC"),
                 path("M46,18L51,24 55,51 48,49z", "#997DB9"),
                 line("M22,24V18C22,6 39,6 39,20V22",INK,3),
                 line("M23,21V17C23,9 37,9 37,20",ICE,1.2), circle(31,37,8,"#F1DAF3"),
                 path("M29,31H33V43H29zM25,35H37V39H25z", "#AF7DC1"), line("M16,46L15,50",ICE)],
    "housing": [rect(12,28,41,27,"#F4CCAE",3,True),
                path("M41,29H52V54H41z", "#D8A787"), rect(41,12,7,15,"#BB9EBD",1,True),
                metal("M6,29L30,9 58,28 53,34 30,18 11,35z", "#F1AEAB", "#BB727D"),
                rect(27,36,12,19,"#A98AC3",2,True), circle(36,45,1,ICE),
                rect(16,34,8,9,"#C0EAF1",1), line("M20,34V43M16,38H24", "#719EB7",1),
                rect(9,54,47,3,"#C2B2CB",1)],
    "transport": [metal("M12,29L19,16Q21,13 27,13H39Q43,13 46,19L53,31z", "#B4B0F3"),
                  path("M22,18H39L45,29H17z", "#C5F3F3", "#6B73A6"), line("M31,18V29", "#7E82B5",1.3),
                  rect(7,29,51,22,"#9C9CE7",6,True), rect(10,45,45,5,"#6B609E",2),
                  rect(12,34,9,6,"#F2EDDC",2), rect(44,34,9,6,"#F2EDDC",2),
                  rect(26,40,13,5,"#D3DFF1",1), circle(17,51,6,INK), circle(48,51,6,INK),
                  circle(17,51,3,"#BBC8E0"),circle(48,51,3,"#BBC8E0"),line("M17,32H47",ICE,1)],
    "utilities": [metal("M17,25C17,4 48,4 48,25Q48,34 40,39V46H24V39Q17,33 17,25z", "#F3D797", "#C4A168"),
                  path("M33,16L25,30H32L29,39 40,25H33z", "#FFF7D6"),
                  rect(23,43,18,12,"#C6CDE4",3,True), line("M25,47H39M25,51H39", "#787F9F",1.2),
                  rect(28,55,8,3,INK,1), line("M14,10L10,6M32,5V2M51,10L55,6", "#DBC79A",2)],
    "entertainment": [metal("M18,22Q32,18 47,22Q53,23 56,42Q59,54 49,52L41,44H24L15,53Q6,55 8,43L12,27Q13,23 18,22z", "#ADA1EA"),
                      rect(16,29,5,15,"#51447F",1),rect(11,34,15,5,"#51447F",1),
                      circle(44,31,3,"#BDE8E2"),circle(51,37,3,"#F1BCCB"),circle(38,37,3,"#DEC8EF"),
                      circle(44,43,3,"#DDD8F5"),circle(29,40,3,INK),circle(35,40,3,INK),line("M21,24Q32,21 43,24",ICE,1)],
    "health": [line("M24,22V15H40V22",INK,4),line("M25,20V16H39V20",ICE,1),
               rect(9,22,47,32,"#C1B4EE",5,True),path("M9,29H56V35H9z", "#E6DDF9"),
               circle(32,38,11,"#F8F2FC"),path("M29,30H35V35H40V41H35V46H29V41H24V35H29z", "#D58DA8"),
               line("M14,48H21M45,48H51",ICE,1.4)],
    "education": [metal("M9,15Q21,10 32,16Q43,10 55,15V51Q43,46 32,53Q22,47 9,51z", "#A99FE8"),
                  path("M12,17Q22,14 30,18V47Q20,43 12,47z", "#F6F2FC"),
                  path("M34,18Q43,14 52,17V47Q42,43 34,47z", "#DCDDF2"),
                  line("M16,23L26,25M16,29L26,31M16,35L26,37M38,23L48,21M38,29L48,27M38,35L48,33", "#A9A4C9",1.3),
                  path("M41,15L46,14V32L43,29 41,33z", "#87C9C7"),line("M32,18V51",INK,1)],
    "travel": [line("M25,18V10H40V18",INK,4),rect(12,17,43,36,"#A5B3E5",5,True),
               path("M19,18H23V53H19zM43,18H47V53H43z", "#D5E1F3"),
               rect(27,28,13,12,"#C5E9E7",2),path("M29,34L37,30 35,35 30,37z", "#669BB3"),
               circle(21,55,3,INK),circle(47,55,3,INK),line("M15,22H50",ICE,1)],
    "salary": [line("M24,21V13H41V21",INK,4),rect(10,21,46,33,"#8C86CC",5,True),
               path("M10,25H56V35Q32,43 10,35z", "#C4BDF0", "#7160AC"),rect(29,33,9,9,"#DBD9EE",2,True),
               rect(34,8,22,15,"#8BCAC0",2,True),circle(45,15,4,"#DBEDE3"),
               line("M38,12H40M51,18H53",ICE,1.3),line("M15,48H25", "#C8C0F0",1)],
    "gift": [rect(12,28,41,27,"#D7ABD9",3,True),rect(8,22,49,11,"#E7BBD9",3,True),
             path("M28,22H36V55H28z", "#B8B0F0"),
             metal("M30,22Q11,24 14,12Q18,3 31,19Q42,3 49,12Q54,24 34,22z", "#C8C1F3"),
             line("M18,13Q24,14 30,21M44,13Q38,15 34,21",ICE,1.3),line("M16,35V49",ICE,1)],
    "other": [rect(10,12,19,19,"#B6B2E6",5,True),rect(35,9,19,19,"#9FD4D4",5,True),
              rect(10,37,19,19,"#DAAFCE",5,True),rect(35,34,19,19,"#B9C5E7",5,True),
              circle(19,21,3,ICE),circle(44,18,3,ICE),circle(19,46,3,ICE),circle(44,43,3,ICE)],
    "people": [circle(24,19,10,"#BEB3EE"),circle(44,22,8,"#A6DBD8"),
               metal("M7,53V44Q8,32 24,32Q40,32 41,44V53z", "#A99CE7"),
               metal("M40,35Q56,34 58,45V53H45V44z", "#A5D5D6"),
               path("M19,13Q24,10 30,15", "#EBE8FC"),line("M13,47V44",ICE,2)],
    "overview": [rect(9,12,20,20,"#B4A5EB",4,True),rect(35,9,20,20,"#B9D7EC",4,True),
                 rect(9,38,20,20,"#ACD8D6",4,True),rect(35,35,20,20,"#C4B3EA",4,True),
                 line("M14,17H24M40,14H50M14,43H24M40,40H50",ICE,1.4)],
    "chart": [rect(8,36,11,20,"#A4D9D4",2,True),rect(26,24,11,32,"#B9A6EB",2,True),
              rect(44,10,11,46,"#9CBEE7",2,True),line("M12,39V52M30,27V52M48,14V52",ICE,1.2),
              line("M6,57H58", "#A3A0C4",1.2)],
}

if __name__ == "__main__":
    for name, pieces in ART.items():
        xml = '<vector xmlns:android="http://schemas.android.com/apk/res/android" xmlns:aapt="http://schemas.android.com/aapt" android:width="32dp" android:height="32dp" android:viewportWidth="64" android:viewportHeight="64">\n'
        xml += "\n".join([shadow(), *pieces]) + "\n</vector>\n"
        prefix = "art_" if name in {"wallet", "bank", "credit", "cash", "people", "overview", "chart"} else "ic_category_"
        (OUT / f"{prefix}{name}.xml").write_text(xml, encoding="utf-8")
    print(f"Generated {len(ART)} original layered vector illustrations.")
