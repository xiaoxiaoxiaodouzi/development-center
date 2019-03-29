!function(e,t){"object"==typeof exports&&"object"==typeof module?module.exports=t():"function"==typeof define&&define.amd?define(t):"object"==typeof exports?exports.JsDiff=t():e.JsDiff=t()}(this,function(){return function(e){function t(r){if(n[r])return n[r].exports
var o=n[r]={exports:{},id:r,loaded:!1}
return e[r].call(o.exports,o,o.exports,t),o.loaded=!0,o.exports}var n={}
return t.m=e,t.c=n,t.p="",t(0)}([function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}t.__esModule=!0
var o=n(1),i=r(o),u=n(3),s=n(4),f=n(5),a=n(6),d=n(7),l=n(8),c=n(9),p=n(10),h=n(12),v=n(13)
t.Diff=i["default"],t.diffChars=u.diffChars,t.diffWords=s.diffWords,t.diffWordsWithSpace=s.diffWordsWithSpace,t.diffLines=f.diffLines,t.diffTrimmedLines=f.diffTrimmedLines,t.diffSentences=a.diffSentences,t.diffCss=d.diffCss,t.diffJson=l.diffJson,t.structuredPatch=p.structuredPatch,t.createTwoFilesPatch=p.createTwoFilesPatch,t.createPatch=p.createPatch,t.applyPatch=c.applyPatch,t.convertChangesToDMP=h.convertChangesToDMP,t.convertChangesToXML=v.convertChangesToXML,t.canonicalize=l.canonicalize},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e){this.ignoreWhitespace=e}function i(e,t,n,r){for(var o=0,i=e.length,u=0,s=0;i>o;o++){var a=e[o]
if(a.removed){if(a.value=n.slice(s,s+a.count).join(""),s+=a.count,o&&e[o-1].added){var d=e[o-1]
e[o-1]=e[o],e[o]=d}}else{if(!a.added&&r){var l=t.slice(u,u+a.count)
l=f["default"](l,function(e,t){var r=n[s+t]
return r.length>e.length?r:e}),a.value=l.join("")}else a.value=t.slice(u,u+a.count).join("")
u+=a.count,a.added||(s+=a.count)}}return e}function u(e){return{newPos:e.newPos,components:e.components.slice(0)}}t.__esModule=!0,t["default"]=o
var s=n(2),f=r(s)
o.prototype={diff:function(e,t,n){function r(e){return n?(setTimeout(function(){n(void 0,e)},0),!0):e}function o(){for(var n=-1*d;d>=n;n+=2){var o=void 0,l=c[n-1],p=c[n+1],h=(p?p.newPos:0)-n
l&&(c[n-1]=void 0)
var v=l&&l.newPos+1<f,g=p&&h>=0&&a>h
if(v||g){if(!v||g&&l.newPos<p.newPos?(o=u(p),s.pushComponent(o.components,void 0,!0)):(o=l,o.newPos++,s.pushComponent(o.components,!0,void 0)),h=s.extractCommon(o,t,e,n),o.newPos+1>=f&&h+1>=a)return r(i(o.components,t,e,s.useLongestToken))
c[n]=o}else c[n]=void 0}d++}var s=this
if(e=this.castInput(e),t=this.castInput(t),t===e)return r([{value:t}])
if(!t)return r([{value:e,removed:!0}])
if(!e)return r([{value:t,added:!0}])
t=this.removeEmpty(this.tokenize(t)),e=this.removeEmpty(this.tokenize(e))
var f=t.length,a=e.length,d=1,l=f+a,c=[{newPos:-1,components:[]}],p=this.extractCommon(c[0],t,e,0)
if(c[0].newPos+1>=f&&p+1>=a)return r([{value:t.join("")}])
if(n)!function v(){setTimeout(function(){return d>l?n():void(o()||v())},0)}()
else for(;l>=d;){var h=o()
if(h)return h}},pushComponent:function(e,t,n){var r=e[e.length-1]
r&&r.added===t&&r.removed===n?e[e.length-1]={count:r.count+1,added:t,removed:n}:e.push({count:1,added:t,removed:n})},extractCommon:function(e,t,n,r){for(var o=t.length,i=n.length,u=e.newPos,s=u-r,f=0;o>u+1&&i>s+1&&this.equals(t[u+1],n[s+1]);)u++,s++,f++
return f&&e.components.push({count:f}),e.newPos=u,s},equals:function(e,t){var n=/\S/
return e===t||this.ignoreWhitespace&&!n.test(e)&&!n.test(t)},removeEmpty:function(e){for(var t=[],n=0;n<e.length;n++)e[n]&&t.push(e[n])
return t},castInput:function(e){return e},tokenize:function(e){return e.split("")}},e.exports=t["default"]},function(e,t){"use strict"
function n(e,t,n){if(Array.prototype.map)return Array.prototype.map.call(e,t,n)
for(var r=Array(e.length),o=0,i=e.length;i>o;o++)r[o]=t.call(n,e[o],o,e)
return r}t.__esModule=!0,t["default"]=n,e.exports=t["default"]},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n){return s.diff(e,t,n)}t.__esModule=!0,t.diffChars=o
var i=n(1),u=r(i),s=new u["default"]
t.characterDiff=s},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n){return a.diff(e,t,n)}function i(e,t,n){return d.diff(e,t,n)}t.__esModule=!0,t.diffWords=o,t.diffWordsWithSpace=i
var u=n(1),s=r(u),f=/^[A-Za-z\xC0-\u02C6\u02C8-\u02D7\u02DE-\u02FF\u1E00-\u1EFF]+$/,a=new s["default"](!0)
t.wordDiff=a
var d=new s["default"]
t.wordWithSpaceDiff=d,a.tokenize=d.tokenize=function(e){for(var t=e.split(/(\s+|\b)/),n=0;n<t.length-1;n++)!t[n+1]&&t[n+2]&&f.test(t[n])&&f.test(t[n+2])&&(t[n]+=t[n+2],t.splice(n+1,2),n--)
return t}},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n){return f.diff(e,t,n)}function i(e,t,n){return a.diff(e,t,n)}t.__esModule=!0,t.diffLines=o,t.diffTrimmedLines=i
var u=n(1),s=r(u),f=new s["default"]
t.lineDiff=f
var a=new s["default"]
t.trimmedLineDiff=a,a.ignoreTrim=!0,f.tokenize=a.tokenize=function(e){for(var t=[],n=e.split(/^/m),r=0;r<n.length;r++){var o=n[r],i=n[r-1],u=i&&i[i.length-1]
"\n"===o&&"\r"===u?t[t.length-1]=t[t.length-1].slice(0,-1)+"\r\n":(this.ignoreTrim&&(o=o.trim(),r<n.length-1&&(o+="\n")),t.push(o))}return t}},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n){return s.diff(e,t,n)}t.__esModule=!0,t.diffSentences=o
var i=n(1),u=r(i),s=new u["default"]
t.sentenceDiff=s,s.tokenize=function(e){return e.split(/(\S.+?[.!?])(?=\s+|$)/)}},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n){return s.diff(e,t,n)}t.__esModule=!0,t.diffCss=o
var i=n(1),u=r(i),s=new u["default"]
t.cssDiff=s,s.tokenize=function(e){return e.split(/([{}:;,]|\s+)/)}},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n){return d.diff(e,t,n)}function i(e,t,n){t=t||[],n=n||[]
var r=void 0
for(r=0;r<t.length;r+=1)if(t[r]===e)return n[r]
var o=void 0
if("[object Array]"===a.call(e)){for(t.push(e),o=Array(e.length),n.push(o),r=0;r<e.length;r+=1)o[r]=i(e[r],t,n)
t.pop(),n.pop()}else if("object"==typeof e&&null!==e){t.push(e),o={},n.push(o)
var u=[],s=void 0
for(s in e)e.hasOwnProperty(s)&&u.push(s)
for(u.sort(),r=0;r<u.length;r+=1)s=u[r],o[s]=i(e[s],t,n)
t.pop(),n.pop()}else o=e
return o}t.__esModule=!0,t.diffJson=o,t.canonicalize=i
var u=n(1),s=r(u),f=n(5),a=Object.prototype.toString,d=new s["default"]
t.jsonDiff=d,d.useLongestToken=!0,d.tokenize=f.lineDiff.tokenize,d.castInput=function(e){return"string"==typeof e?e:JSON.stringify(i(e),void 0,"  ")},d.equals=function(e,t){return s["default"].prototype.equals(e.replace(/,([\r\n])/g,"$1"),t.replace(/,([\r\n])/g,"$1"))}},function(e,t){"use strict"
function n(e,t){for(var n=t.split("\n"),r=[],o=0,i=!1,u=!1;o<n.length&&!/^@@/.test(n[o]);)o++
for(;o<n.length;o++)if("@"===n[o][0]){var s=n[o].split(/@@ -(\d+),(\d+) \+(\d+),(\d+) @@/)
r.unshift({start:s[3],oldlength:+s[2],removed:[],newlength:s[4],added:[]})}else"+"===n[o][0]?r[0].added.push(n[o].substr(1)):"-"===n[o][0]?r[0].removed.push(n[o].substr(1)):" "===n[o][0]?(r[0].added.push(n[o].substr(1)),r[0].removed.push(n[o].substr(1))):"\\"===n[o][0]&&("+"===n[o-1][0]?i=!0:"-"===n[o-1][0]&&(u=!0))
var f=e.split("\n")
for(o=r.length-1;o>=0;o--){for(var a=r[o],d=0;d<a.oldlength;d++)if(f[a.start-1+d]!==a.removed[d])return!1
Array.prototype.splice.apply(f,[a.start-1,a.oldlength].concat(a.added))}if(i)for(;!f[f.length-1];)f.pop()
else u&&f.push("")
return f.join("\n")}t.__esModule=!0,t.applyPatch=n},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}function o(e,t,n,r,o,i,u){function f(e){return a["default"](e,function(e){return" "+e})}u||(u={context:4})
var d=s.patchDiff.diff(n,r)
d.push({value:"",lines:[]})
for(var l=[],c=0,p=0,h=[],v=1,g=1,m=function(e){var t=d[e],o=t.lines||t.value.replace(/\n$/,"").split("\n")
if(t.lines=o,t.added||t.removed){if(!c){var i=d[e-1]
c=v,p=g,i&&(h=u.context>0?f(i.lines.slice(-u.context)):[],c-=h.length,p-=h.length)}h.push.apply(h,a["default"](o,function(e){return(t.added?"+":"-")+e})),t.added?g+=o.length:v+=o.length}else{if(c)if(o.length<=2*u.context&&e<d.length-2)h.push.apply(h,f(o))
else{var s=Math.min(o.length,u.context)
h.push.apply(h,f(o.slice(0,s)))
var m={oldStart:c,oldLines:v-c+s,newStart:p,newLines:g-p+s,lines:h}
if(e>=d.length-2&&o.length<=u.context){var _=/\n$/.test(n),w=/\n$/.test(r)
0!=o.length||_?_&&w||h.push("\\ No newline at end of file"):h.splice(m.oldLines,0,"\\ No newline at end of file")}l.push(m),c=0,p=0,h=[]}v+=o.length,g+=o.length}},_=0;_<d.length;_++)m(_)
return{oldFileName:e,newFileName:t,oldHeader:o,newHeader:i,hunks:l}}function i(e,t,n,r,i,u,s){var f=o(e,t,n,r,i,u,s),a=[]
e==t&&a.push("Index: "+e),a.push("==================================================================="),a.push("--- "+f.oldFileName+(void 0===f.oldHeader?"":"	"+f.oldHeader)),a.push("+++ "+f.newFileName+(void 0===f.newHeader?"":"	"+f.newHeader))
for(var d=0;d<f.hunks.length;d++){var l=f.hunks[d]
a.push("@@ -"+l.oldStart+","+l.oldLines+" +"+l.newStart+","+l.newLines+" @@"),a.push.apply(a,l.lines)}return a.join("\n")+"\n"}function u(e,t,n,r,o,u){return i(e,e,t,n,r,o,u)}t.__esModule=!0,t.structuredPatch=o,t.createTwoFilesPatch=i,t.createPatch=u
var s=n(11),f=n(2),a=r(f)},function(e,t,n){"use strict"
function r(e){return e&&e.__esModule?e:{"default":e}}t.__esModule=!0
var o=n(1),i=r(o),u=new i["default"]
t.patchDiff=u,u.tokenize=function(e){var t=[],n=e.split(/(\n|\r\n)/)
n[n.length-1]||n.pop()
for(var r=0;r<n.length;r++){var o=n[r]
r%2?t[t.length-1]+=o:t.push(o)}return t}},function(e,t){"use strict"
function n(e){for(var t=[],n=void 0,r=void 0,o=0;o<e.length;o++)n=e[o],r=n.added?1:n.removed?-1:0,t.push([r,n.value])
return t}t.__esModule=!0,t.convertChangesToDMP=n},function(e,t){"use strict"
function n(e){for(var t=[],n=0;n<e.length;n++){var o=e[n]
o.added?t.push("<ins>"):o.removed&&t.push("<del>"),t.push(r(o.value)),o.added?t.push("</ins>"):o.removed&&t.push("</del>")}return t.join("")}function r(e){var t=e
return t=t.replace(/&/g,"&amp;"),t=t.replace(/</g,"&lt;"),t=t.replace(/>/g,"&gt;"),t=t.replace(/"/g,"&quot;")}t.__esModule=!0,t.convertChangesToXML=n}])})
