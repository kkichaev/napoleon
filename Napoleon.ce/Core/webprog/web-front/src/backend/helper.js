export function getOfferLink(country) {
  var file = "offerge.pdf";
  if (["RUS", "BLR"].includes(country)) file = "offerrus.pdf";

  return "/" + file;
}

const _currency = {
  RUB: "\u20BD",
  // KZT: "\u20B8",
};
export function getCurrency(id) {
  if (id in _currency) return _currency[id];
  return "\u0024";
}

export function fmtDate(val) {
  if (val)
    return `${val.substring(6, 8)}.${val.substring(4, 6)}.${val.substring(
      0,
      4
    )}`;
  else "";
}

export function mailto() {
  return "mailto:info@grsoft.app";
}

export function testEmail(email) {
  return email && email.length > 5;
}

export function fmtTime(val) {
  if (val)
    return `${val.substring(8, 10)}:${val.substring(10, 12)}:${val.substring(
      12,
      14
    )}`;
  else "";
}
