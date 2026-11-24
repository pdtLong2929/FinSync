"""Inspect bundled font cmap/feature tables without platform font fallback or libraries."""
from pathlib import Path
import struct
import unicodedata

ROOT = Path(__file__).resolve().parents[1] / "app/src/main/res/font"
SAMPLE = "Nguyễn Phú Đạt – Tổng số dư · Đăng nhập · Thu nhập · Chi tiêu · 12.450.000 ₫"


def audit(font):
    data = font.read_bytes()
    u16 = lambda pos: struct.unpack_from(">H", data, pos)[0]
    u32 = lambda pos: struct.unpack_from(">I", data, pos)[0]
    tables = {data[p:p+4].decode(): u32(p+8) for p in range(12, 12+u16(4)*16, 16)}
    cmap = tables["cmap"]
    subtables = [cmap+u32(cmap+4+i*8+4) for i in range(u16(cmap+2))]

    def glyph(code):
        for sub in subtables:
            fmt = u16(sub)
            if fmt == 12:
                for i in range(u32(sub+12)):
                    start, end, first = struct.unpack_from(">III", data, sub+16+i*12)
                    if start <= code <= end:
                        return first+code-start
            elif fmt == 4 and code <= 0xffff:
                count = u16(sub+6)//2
                ends, starts = sub+14, sub+16+count*2
                deltas, offsets = starts+count*2, starts+count*4
                for i in range(count):
                    if u16(starts+2*i) <= code <= u16(ends+2*i):
                        delta, offset = u16(deltas+2*i), u16(offsets+2*i)
                        if not offset:
                            result = (code+delta) & 0xffff
                        else:
                            result = u16(offsets+2*i+offset+2*(code-u16(starts+2*i)))
                            if result:
                                result = (result+delta) & 0xffff
                        if result:
                            return result
        return 0

    needed = set(SAMPLE) - {" "}
    missing = sorted(c for c in needed if not glyph(ord(c)))
    features = set()
    if "GSUB" in tables:
        feature = tables["GSUB"]+u16(tables["GSUB"]+6)
        features = {data[feature+2+i*6:feature+6+i*6].decode() for i in range(u16(feature))}
    hmtx = tables["hmtx"]
    advances = [u16(hmtx+glyph(ord(c))*4) for c in "0123456789"]
    tabular = "tnum" in features or len(set(advances)) == 1
    print(f"{font.name}: VN NFC + VND glyphs={'PASS' if not missing else [hex(ord(c)) for c in missing]}; tabular figures={'available' if tabular else 'proportional'}")
    assert not missing
    if font.name.startswith(("manrope", "space_grotesk")):
        assert tabular, "Financial display family requires tabular numerals"


if __name__ == "__main__":
    for font in sorted(ROOT.glob("*.ttf")):
        audit(font)
