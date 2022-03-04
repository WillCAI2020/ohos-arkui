
class rgbhsv {
    // arr: rgb数组
    rgb2hsv(arr) {
        let rr;
        let gg;
        let bb;
        let r = arr[0] / 255;
        let g = arr[1] / 255;
        let b = arr[2] / 255;
        let h;
        let s;
        let v = Math.max(r, g, b);
        let diff = v - Math.min(r, g, b);
        let diffc = function (c) {
            return (v - c) / 6 / diff + 1 / 2;
        };

        if (diff === 0) {
            h = s = 0;
        } else {
            s = diff / v;
            rr = diffc(r);
            gg = diffc(g);
            bb = diffc(b);

            if (r === v) {
                h = bb - gg;
            } else if (g === v) {
                h = (1 / 3) + rr - bb;
            } else if (b === v) {
                h = (2 / 3) + gg - rr;
            }
            if (h < 0) {
                h += 1;
            } else if (h > 1) {
                h -= 1;
            }
        }
        return [Math.round(h * 360), Math.round(s * 100), Math.round(v * 100)]
    }
    hsv2rgb (hsv) {
        let _l = hsv[0];
        let _m = hsv[1];
        let _n = hsv[2];
        let newR;
        let newG;
        let newB;
        if (_m === 0) {
            _l = _m = _n = Math.round(255 * _n / 100);
            newR = _l;
            newG = _m;
            newB = _n;
        } else {
            _m = _m / 100;
            _n = _n / 100;
            let p = Math.floor(_l / 60) % 6;
            let f = _l / 60 - p;
            let a = _n * (1 - _m);
            let b = _n * (1 - _m * f);
            let c = _n * (1 - _m * (1 - f));
            switch (p) {
                case 0:
                    newR = _n; newG = c; newB = a;
                    break;
                case 1:
                    newR = b; newG = _n; newB = a;
                    break;
                case 2:
                    newR = a; newG = _n; newB = c;
                    break;
                case 3:
                    newR = a; newG = b; newB = _n;
                    break;
                case 4:
                    newR = c; newG = a; newB = _n;
                    break;
                case 5:
                    newR = _n; newG = a; newB = b;
                    break;
            }
            newR = Math.round(255 * newR);
            newG = Math.round(255 * newG);
            newB = Math.round(255 * newB);
        }
        return [newR, newG, newB]
    }
}
export default rgbhsv;
