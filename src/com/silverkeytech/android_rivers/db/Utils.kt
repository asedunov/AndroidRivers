/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package com.silverkeytech.android_rivers.db

import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.android_rivers.outlines.Body
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline

public fun getBookmarkCollectionFromDb() : List<BookmarkCollection>{
    var coll = DatabaseManager.query().bookmarkCollection().all()

    if (coll.exist)
        return coll.values!!
    else
        return arrayListOf<BookmarkCollection>()
}

public fun getBookmarksFromDbByCollection(collectionId : Int) : List<Bookmark>{
    var bookmarks = DatabaseManager.query().bookmark().byCollectionId(collectionId)

    if (bookmarks.exist)
        return bookmarks.values!!
    else
        return arrayListOf<Bookmark>()
}

public fun getBookmarksFromDb(kind : BookmarkKind) : List<Bookmark>{
    var bookmarks = DatabaseManager.query().bookmark().byKind(kind)

    if (bookmarks.exist)
        return bookmarks.values!!
    else
        return arrayListOf<Bookmark>()
}

//get bookmarks from db and return the data in opml format
public fun getBookmarksFromDbAsOpml(kind : BookmarkKind): Opml {
    var opml = Opml()
    opml.body = Body()

    var bookmarks = DatabaseManager.query().bookmark().byKind(kind)

    if (bookmarks.exist){
        for(val b in bookmarks.values?.iterator()){
            var o = Outline()
            o.text = b.title
            o.url = b.url
            o.language = b.language
            opml.body!!.outline!!.add(o)
        }
    }
    return opml
}

public fun saveOpmlAsBookmarks(opml: Opml): Result<Opml> {
    try{
        val bkDao = DatabaseManager.bookmark!!

        for(val o in opml.body!!.outline!!.iterator()){
            val b = Bookmark()
            b.title = o.text!!
            b.url = o.url!!
            if (!o.language!!.isNullOrEmpty())
                b.language = o.language!!

            b.kind = BookmarkKind.RIVER.toString()

            bkDao.create(b)
        }

        return Result.right(opml)
    }
    catch (e: Exception)
    {
        return Result.wrong(e)
    }
}