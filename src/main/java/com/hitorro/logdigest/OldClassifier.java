/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.logdigest;

import com.hitorro.logdigest.requestclassifier.Classifier;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.string.StringUtil;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 3, 2008 Time: 10:13:18 AM Old line
 * processing code.
 */
public class OldClassifier {
    public static byte handleRequestAndMediaTypeByte(String requestURI, int timestamp, NewLogLine entry) {
        byte requestType = Classifier.NA;
        int ix = -1;
        String mediaType = null;

        if (requestURI.indexOf("HiTorro_beacon.gif") > 0) {
            // /origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/HiTorro_beacon.gif?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22
            requestType = Classifier.Beacon;
        } else if (requestURI.indexOf("HiTorro-player.swf") > 0 ||
                requestURI.indexOf("overlay.swf") > 0 ||
                requestURI.indexOf("player/popup.js") > 0 ||
                requestURI.indexOf("player/popup.php") > 0 ||
                requestURI.indexOf("player/email") > 0) {
            ///origin-www.HiTorro.net/player/HiTorro-player.swf?bc=3F34K2L1
            requestType = Classifier.Player;
        } else if (requestURI.indexOf("breadcrumb/provisionChild") > 0) {
            // /origin-services.HiTorro.net/breadcrumb/provisionChild?parent=bc9d1e39c494440e8d963774c3bee8fc
            requestType = Classifier.ProvisionChild;
        } else if (requestURI.indexOf("breadcrumb/playlist") > 0) {
            // /origin-services.HiTorro.net/breadcrumb/playlist/925ba603a5804f7b98562231b629a2b5/PID_011808
            requestType = Classifier.Playlist;
        } else if (requestURI.indexOf("ping/hostingsite") > 0) {
            // /origin-services.HiTorro.net/ping/hostingsite?breadcrumb=925ba603a5804f7b98562231b629a2b5&hosturl=http%3A%2F%2Fwww%2Ejaiku%2Ecom%2Fblog%2F
            requestType = Classifier.Hostingsite;
        } else if (requestURI.indexOf("ping/useraction") > 0) {
            // origin-services.HiTorro.net/ping/useraction?action=play&breadcrumb=f007c9881bed4c4dbc9983fd37815b03&mediaId=PID_013171&time=0
            if (requestURI.indexOf("action=playlist") > 0) {
                requestType = Classifier.Action;
            } else if (requestURI.indexOf("action=stop") > 0) {
                requestType = Classifier.Action;
            } else if (requestURI.indexOf("action=play") > 0) {
                requestType = Classifier.Action;
            } else if (requestURI.indexOf("action=clickthrough") > 0) {
                requestType = Classifier.Action;
            } else {
                requestType = Classifier.Action;
            }
        } else if ((ix = requestURI.indexOf(".flv")) > 0 ||
                (ix = requestURI.indexOf(".mp3")) > 0 ||
                (ix = requestURI.indexOf(".mov")) > 0 ||
                (ix = requestURI.indexOf(".m4v")) > 0 ||
                (ix = requestURI.indexOf(".mp4")) > 0 ||
                (ix = requestURI.indexOf(".wmv")) > 0 ||
                (ix = requestURI.indexOf(".FLV")) > 0 ||
                (ix = requestURI.indexOf(".MP3")) > 0 ||
                (ix = requestURI.indexOf(".MOV")) > 0 ||
                (ix = requestURI.indexOf(".M4V")) > 0 ||
                (ix = requestURI.indexOf(".MP4")) > 0 ||
                (ix = requestURI.indexOf(".WMV")) > 0) {
            requestType = Classifier.Download;
            // /origin-www.HiTorro.net/aplayer/audio_player_slim.swf?playlist_url=http://www.HiTorro.net/audio/markradcliffe/AUDIO000.MP3.xspf%20&player_title=HiTorro.Net%20-%20Mark%20Radcliffe%20-%20Open%20Source%20Legal%20Guru
            mediaType = requestURI.substring(ix + 1, ix + 4).toLowerCase();
        } else if (requestURI.indexOf(".gif") > 0 ||
                requestURI.indexOf(".jpg") > 0 ||
                requestURI.indexOf(".html") > 0 ||
                requestURI.indexOf(".css") > 0 ||
                requestURI.indexOf(".js") > 0 ||
                requestURI.indexOf(".png") > 0 ||
                requestURI.indexOf(".swf") > 0 ||
                requestURI.indexOf("robots.txt") > 0 ||
                requestURI.indexOf("favicon.ico") > 0 ||
                requestURI.indexOf("crossdomain.xml") > 0 ||
                requestURI.indexOf("wp-content/themes") > 0 ||
                requestURI.indexOf("wp-content/plugins") > 0) {
            // origin-iphone.HiTorro.net/techone/iui/options-extend.png
            requestType = Classifier.Resource;
        } else if (requestURI.indexOf("/feed") > 0 ||
                requestURI.indexOf("wp-rss2.php") > 0 ||
                requestURI.indexOf("wp-feed.php") > 0 ||
                requestURI.indexOf("feed=") > 0 ||
                requestURI.indexOf("www.HiTorro.net/ptfeeds") > 0 ||
                requestURI.indexOf("www.HiTorro.net/?feed%3Drss2") > 0) {
            // /origin-www.HiTorro.net/wp-rss2.php?category_name=InfoTalk&feed=rss2
            requestType = Classifier.Feed;
        } else if (requestURI.indexOf("media.HiTorro.net") > 0) {
            //
            //requestType = "mediaother";
        } else if (requestURI.indexOf("wp-comments-post.php") > 0) {

            // /origin-www.HiTorro.net/scobleshow/wp-comments-post.php
            requestType = Classifier.Comment;
        } else if (requestURI.indexOf("player.swd") > 0 ||
                requestURI.indexOf("overlay.swd") > 0 ||
                requestURI.indexOf("breadcrumb/hostingsite") > 0 ||
                requestURI.indexOf("player/null") > 0) {
            // /origin-www.HiTorro.net/player/null
            //requestType = "junk";
        }

        if (requestType == Classifier.NA) {
            // all the other requests are (probably) looking at wordpress post pages, searches, etc.
            //  origin-iphone.HiTorro.net/rebootshow/news_item.php?guid=http://0xg3458.hub.io/pb.php?
            //requestType = Classifier.Postpage;
            //Log.logdigest.info("postpage: %s", requestURI);
        }

        //entry.setRequestType(_lookupRequestType.add(requestType, timestamp));
        if (mediaType != null) {
            //   entry.setMediaType(_lookupMediaType.add(mediaType, timestamp));
        }
        return requestType;
    }

    public static final void handleBreadcrumbAndReferer(NewLogLine ll, String requestURI, int timeStamp, String referer) {
        // check if there is breadcrumb information
        String bc = null;
        int indx = -1;
        if (requestURI == null) {
            //XXX
            return;
        }
        if (requestURI.indexOf("playlist.php") >= 0 ||
                requestURI.indexOf("breadcrumb/playlist") >= 0) {
            // this has a playlist request in it
            // /origin-www.HiTorro.net/player/playlist.php
            indx = requestURI.indexOf("breadcrumb/playlist/");
            if (indx >= 0) {
                // normal playlist request
                // there may be a mediaId token after the breadcrumb
                int slashIndx = requestURI.indexOf('/', indx + 20);
                if (slashIndx < 0) {
                    // just take the rest of the line
                    slashIndx = requestURI.length();
                }
                // /origin-services.HiTorro.net/breadcrumb/playlist/925ba603a5804f7b98562231b629a2b5/PID_011808
                bc = requestURI.substring(indx + 20, slashIndx);
            } else {
                // must be a playlist.php reference
                // origin-www.HiTorro.net/player/playlist.php
                indx = requestURI.indexOf("breadcrumb=");
                bc = requestURI.substring(indx + 11);
            }
            //System.out.println(linenumber + "Request URI: " + lline.getRequestURI());
            //count++;
        } else if ((indx = requestURI.indexOf("HiTorro-player.swf?bc=")) >= 0) {
            // origin-www.HiTorro.net/player/HiTorro-player.swf?bc=3F34K2L1
            indx += 22;
            int spindx = requestURI.indexOf(' ', indx);
            if (spindx > 0) {
                bc = requestURI.substring(indx, spindx);
            }
        } else if (requestURI.indexOf("popup.php?content") > 0) {
            //  /origin-www.HiTorro.net/player/popup.php?content=http://media1.HiTorro.net/media/2007/07/PID_011951/HiTorro_IBM_Ventures.flv&totalTime=3335000&postURL=http://www.HiTorro.net/home/3635/meeting-ibm-ventures%20%20%20%20%20%20%20%20%20%20%20%20&breadcrumb=68f1d1f97ea64a70a64cacf9e737288e&mp3ImageSrc=http://media1.HiTorro.net/media/2007/07/PID_011951/HiTorro_IBM_Ventures.jpg&permalink=http://www.HiTorro.net/home/3635/meeting-ibm-ventures&commentsLink=undefined&ispopup=true
            indx = requestURI.indexOf("breadcrumb=");
            if (indx > 0) {
                int ampIndx = requestURI.indexOf('&', indx);
                if (ampIndx > 0) {
                    bc = requestURI.substring(indx + 11, ampIndx);
                }
            }
        } else if (requestURI.indexOf("breadcrumb/hostingsite") >= 0 ||
                requestURI.indexOf("ping/hostingsite") >= 0) {
            // this is a hosting site request
            // /origin-services.HiTorro.net/ping/hostingsite?breadcrumb=925ba603a5804f7b98562231b629a2b5&hosturl=http%3A%2F%2Fwww%2Ejaiku%2Ecom%2Fblog%2F
            int breqIndex = requestURI.indexOf("breadcrumb=");
            if (breqIndex > 0) {
                breqIndex += 11; // advance to start of breadcrumb
                int ampIndex = requestURI.indexOf('&', breqIndex);
                if (ampIndex > 0) {
                    bc = requestURI.substring(breqIndex, ampIndex);
                    // in this case we also have a referer - use it if we don't already have one
                    if (referer == null || referer.length() < 7) {
                        int hosteqIndex = requestURI.indexOf("hosturl=");
                        if (hosteqIndex > 0) {
                            hosteqIndex += 8;
                            ampIndex = requestURI.indexOf('&', hosteqIndex);
                            if (ampIndex < 0) {
                                ampIndex = requestURI.length();
                            }
                            referer = requestURI.substring(hosteqIndex, ampIndex);
                            referer = StringUtil.decodeUrl(referer);
                        }
                    }
                }
            }
        } else if (requestURI.indexOf("breadcrumb/provisionChild") >= 0) {
            // this is a provisionChild call - look for the parent query parameter

            // origin-services.HiTorro.net/breadcrumb/provisionChild?parent=bc9d1e39c494440e8d963774c3bee8fc
            // referer of: http://www.HiTorro.net/player/HiTorro-player.swf?bc=bc9d1e39c494440e8d963774c3bee8fc
            indx = requestURI.indexOf("parent=");
            if (indx >= 0) {
                indx += 7; // move past parent=
                int end = requestURI.indexOf("&", indx);
                if (end < 0) {
                    end = requestURI.length();
                }
                bc = requestURI.substring(indx, end);
            }
        } else if (requestURI.indexOf("ping/useraction") >= 0) {
            // user action tracking - should have a breadcrumb
            // /origin-services.HiTorro.net/ping/useraction?action=play&breadcrumb=f007c9881bed4c4dbc9983fd37815b03&mediaId=PID_013171&time=0
            // referrer of: http://www.HiTorro.net/player/HiTorro-player.swf?bc=4d3ddbc5302c45c5a4df99544a97affa
            indx = requestURI.indexOf("&breadcrumb=");
            if (indx >= 0) {
                indx += 12; // move past breadcrumb=
                int end = requestURI.indexOf("&", indx);
                if (end < 0) {
                    end = requestURI.length();
                }
                bc = requestURI.substring(indx, end);
            }
        }

        // now that we have (possibly) breadcrumb and referer values, get them into the entry


        if (!StringUtil.nullOrEmptyString(bc)) {
            //Log.logdigest.info("BC not null: %s, %s", referer, requestURI);
            //int breadcrumbId = _lookupBreadcrumb.add(bc, timeStamp);
            //entry.setBreadcrumbId(breadcrumbId);
        }
        if (referer != null) {
            //Log.logdigest.info("Referrer not null: %s, %s", referer, requestURI);
            //int referrerId = _lookupReferrer.add(referer, timeStamp);
            //entry.setReferrerId(referrerId);
            //int domainId = _lookupDomainShort.add(referer, timeStamp);
            //entry.setReferrerDomainId(domainId);
        }
    }


    public static final int handlePid(String requesturi, int timestamp) {
        // the character we expect to end the mediaId token:
        char endChar = '/';

        // check for the regular media read
        int indx = requesturi.indexOf("popup.php");
        if (indx < 0) {
            // check for a player with content tag
            indx = requesturi.indexOf("HiTorro-player.swf");
            if (indx >= 0) {
                indx = requesturi.indexOf("content", indx);
            }
        }
        if (indx < 0) {
            // check for a user action
            indx = requesturi.indexOf("ping/useraction");
            // user actions have the mediaid embeded in the query parameters, so
            if (indx >= 0) {
                endChar = '&';
            }
        }
        if (indx < 0) {
            // check for a provisionChild with a media field
            indx = requesturi.indexOf("breadcrumb/provisionChild");
            if (indx >= 0) {
                indx = requesturi.indexOf("mediaId");
            }
        }
        if (indx < 0) {
            // check for a playlist with a media field
            indx = requesturi.indexOf("breadcrumb/playlist");
            if (indx >= 0) {
                indx = requesturi.indexOf("mediaId");
            }
        }

        if (indx < 0) {
            // we didn't find an interesting line, so no HiTorro id
            return -1;
        }

        // extract the HiTorro id
        // at present we can count on the PID_ prefix
        indx = requesturi.indexOf("PID_", indx);
        if (indx >= 0) {
            int end = requesturi.indexOf(endChar, indx);
            if (end < 0) {
                end = requesturi.length();
            }
            String pid = requesturi.substring(indx + 4, end);
            //return _lookupPid.add(pid, timestamp);
            try {
                return Integer.parseInt(pid);
            } catch (Exception e) {
                //  Bad parsing on this since pid is not at end: origin-www.HiTorro.net/home?s=%3Cembed+type%3D%22application%2Fx-shockwave-flash%22+src%3D%22http%3A%2F%2Fwww.HiTorro.net%2Fplayer%2FHiTorro-player.swf%3Fbc%3D3F34K2L1%22+flashvars%3D%22content%3Dhttp%3A%2F%2Fmedia1.HiTorro.net%2Fmedia%2F2007%2F02%2FPID_010264%2FHiTorro_ScobleShow_WebSister.flv%26totalTime%3D1509000%26postURL%3Dhttp%3A%2F%2Fwww.HiTorro.net%26breadcrumb%3D3F34K2L1%22+height%3D%22269%22+width%3D%22320%22+%2F%3E&x=17&y=7
                Console.println();
            }
        }

        return -1;
    }

    public static final String handleRequestAndMediaType(String requestURI, int timestamp, NewLogLine entry) {
        String requestType = null;
        int ix = -1;
        String mediaType = null;

        if (requestURI.indexOf("HiTorro_beacon.gif") > 0) {
            // /origin-www.HiTorro.net/home/technology/2149/%5C%22http:/media1.HiTorro.net/graphics/HiTorro_beacon.gif?p=/home/technology/2149/netgear-introduces-gigabit-speed-to-their-stackable-smart-switches%5C%22
            requestType = "baseui";
        } else if (requestURI.indexOf("HiTorro-player.swf") > 0 ||
                requestURI.indexOf("overlay.swf") > 0 ||
                requestURI.indexOf("player/popup.js") > 0 ||
                requestURI.indexOf("player/popup.php") > 0 ||
                requestURI.indexOf("player/email") > 0) {
            ///origin-www.HiTorro.net/player/HiTorro-player.swf?bc=3F34K2L1
            requestType = "player";
        } else if (requestURI.indexOf("breadcrumb/provisionChild") > 0) {
            // /origin-services.HiTorro.net/breadcrumb/provisionChild?parent=bc9d1e39c494440e8d963774c3bee8fc
            requestType = "provisionChild";
        } else if (requestURI.indexOf("breadcrumb/playlist") > 0) {
            // /origin-services.HiTorro.net/breadcrumb/playlist/925ba603a5804f7b98562231b629a2b5/PID_011808
            requestType = "playlist";
        } else if (requestURI.indexOf("ping/hostingsite") > 0) {
            // /origin-services.HiTorro.net/ping/hostingsite?breadcrumb=925ba603a5804f7b98562231b629a2b5&hosturl=http%3A%2F%2Fwww%2Ejaiku%2Ecom%2Fblog%2F
            requestType = "hostingsite";
        } else if (requestURI.indexOf("ping/useraction") > 0) {
            // origin-services.HiTorro.net/ping/useraction?action=play&breadcrumb=f007c9881bed4c4dbc9983fd37815b03&mediaId=PID_013171&time=0
            if (requestURI.indexOf("action=playlist") > 0) {
                requestType = "useraction-playlist";
            } else if (requestURI.indexOf("action=stop") > 0) {
                requestType = "useraction-stop";
            } else if (requestURI.indexOf("action=play") > 0) {
                requestType = "useraction-play";
            } else if (requestURI.indexOf("action=clickthrough") > 0) {
                requestType = "useraction-clickthrough";
            } else {
                requestType = "useraction-other";
            }
        } else if ((ix = requestURI.indexOf(".flv")) > 0 ||
                (ix = requestURI.indexOf(".mp3")) > 0 ||
                (ix = requestURI.indexOf(".mov")) > 0 ||
                (ix = requestURI.indexOf(".m4v")) > 0 ||
                (ix = requestURI.indexOf(".mp4")) > 0 ||
                (ix = requestURI.indexOf(".wmv")) > 0 ||
                (ix = requestURI.indexOf(".FLV")) > 0 ||
                (ix = requestURI.indexOf(".MP3")) > 0 ||
                (ix = requestURI.indexOf(".MOV")) > 0 ||
                (ix = requestURI.indexOf(".M4V")) > 0 ||
                (ix = requestURI.indexOf(".MP4")) > 0 ||
                (ix = requestURI.indexOf(".WMV")) > 0) {
            requestType = "download";
            // /origin-www.HiTorro.net/aplayer/audio_player_slim.swf?playlist_url=http://www.HiTorro.net/audio/markradcliffe/AUDIO000.MP3.xspf%20&player_title=HiTorro.Net%20-%20Mark%20Radcliffe%20-%20Open%20Source%20Legal%20Guru
            mediaType = requestURI.substring(ix + 1, ix + 4).toLowerCase();
        } else if (requestURI.indexOf(".gif") > 0 ||
                requestURI.indexOf(".jpg") > 0 ||
                requestURI.indexOf(".html") > 0 ||
                requestURI.indexOf(".css") > 0 ||
                requestURI.indexOf(".js") > 0 ||
                requestURI.indexOf(".png") > 0 ||
                requestURI.indexOf(".swf") > 0 ||
                requestURI.indexOf("robots.txt") > 0 ||
                requestURI.indexOf("favicon.ico") > 0 ||
                requestURI.indexOf("crossdomain.xml") > 0 ||
                requestURI.indexOf("wp-content/themes") > 0 ||
                requestURI.indexOf("wp-content/plugins") > 0) {
            // origin-iphone.HiTorro.net/techone/iui/options-extend.png
            requestType = "resource";
        } else if (requestURI.indexOf("/feed") > 0 ||
                requestURI.indexOf("wp-rss2.php") > 0 ||
                requestURI.indexOf("wp-feed.php") > 0 ||
                requestURI.indexOf("feed=") > 0 ||
                requestURI.indexOf("www.HiTorro.net/ptfeeds") > 0 ||
                requestURI.indexOf("www.HiTorro.net/?feed%3Drss2") > 0) {
            // /origin-www.HiTorro.net/wp-rss2.php?category_name=InfoTalk&feed=rss2
            requestType = "feed";
        } else if (requestURI.indexOf("media.HiTorro.net") > 0) {
            //
            requestType = "mediaother";
        } else if (requestURI.indexOf("wp-comments-post.php") > 0) {

            // /origin-www.HiTorro.net/scobleshow/wp-comments-post.php
            requestType = "comment";
        } else if (requestURI.indexOf("player.swd") > 0 ||
                requestURI.indexOf("overlay.swd") > 0 ||
                requestURI.indexOf("breadcrumb/hostingsite") > 0 ||
                requestURI.indexOf("player/null") > 0) {
            // /origin-www.HiTorro.net/player/null
            requestType = "junk";
        }

        if (requestType == null) {
            // all the other requests are (probably) looking at wordpress post pages, searches, etc.
            //  origin-iphone.HiTorro.net/rebootshow/news_item.php?guid=http://0xg3458.hub.io/pb.php?
            requestType = "postpage";
            //Log.logdigest.info("postpage: %s", requestURI);
        }

        //entry.setRequestType(_lookupRequestType.add(requestType, timestamp));
        if (mediaType != null) {
            //   entry.setMediaType(_lookupMediaType.add(mediaType, timestamp));
        }
        return requestType;
    }
}
