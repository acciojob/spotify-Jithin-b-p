package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {

        for(User user: users){

            if(user.getName().equals(name)){
                return user;
            }
        }
        User user = new User(name, mobile);
        users.add(user);
        userPlaylistMap.put(user, new ArrayList<>());

        return user;
    }

    public Artist createArtist(String name) {

        for(Artist artist: artists){

            if(artist.getName().equals(name)){
                return artist;
            }
        }

        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist, new ArrayList<>());

        return artist;
    }

    public Album createAlbum(String title, String artistName) {

        //if album already exists.
        for(Album album: albums){

            if(album.getTitle().equals(title)){

                return album;

            }

        }

        //checking if the artist exists.
        boolean artistExist = false;
        Artist artistNeeded = null;
        for(Artist artist: artists){

            if(artist.getName().equals(artistName)){
                artistExist = true;
                artistNeeded = artist;
                break;
            }
        }

        //if artist doesnt exist.
        if(!artistExist){

            artistNeeded = new Artist(artistName);
            artists.add(artistNeeded);
            artistAlbumMap.put(artistNeeded, new ArrayList<>());

        }

        //adding album.
        Album album = new Album(title);
        albums.add(album);

        //adding album to artist.
        artistAlbumMap.get(artistNeeded).add(album);

        albumSongMap.put(album, new ArrayList<>());

        return album;

    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        boolean albumExist = false;
        Album albumNeeded = null;
        for(Album album: albums){

            if(album.getTitle().equals(albumName)){

                albumExist = true;
                albumNeeded = album;
                break;
            }
        }

        if(!albumExist){
            throw new Exception("Album does not exist");
        }

        Song song = new Song(title, length);

        //add song.
        songs.add(song);

        //add song to album.
        albumSongMap.get(albumNeeded).add(song);

        //song liked user map
        songLikeMap.put(song, new ArrayList<>());

        return song;

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        //if playList already exists.
        for(Playlist playlist: playlists){

            if(playlist.getTitle().equals(title)){

                return playlist;

            }
        }

        //find user.
        boolean userExist = false;
        User creatorOfPlaylist = null;
        for(User user: users){

            if(user.getMobile().equals(mobile)){

                userExist = true;
                creatorOfPlaylist = user;
                break;

            }
        }

        //user not exist.
        if(!userExist){

            throw new Exception("User does not exist");

        }


        //creating playlist.
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        //creator playlist mapping.
        creatorPlaylistMap.put(creatorOfPlaylist, playlist);

        //user playList mapping.
        userPlaylistMap.put(creatorOfPlaylist, new ArrayList<>());
        userPlaylistMap.get(creatorOfPlaylist).add(playlist);

        //adding current listener(creator)
        playlistListenerMap.put(playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(creatorOfPlaylist);

        List<Song> songsList = new ArrayList<>();
        //adding songs.
        for(Song song: songs){
            if(song.getLength() == length){

                songsList.add(song);

            }
        }

        //creating playlist with songs list DB.
        playlistSongMap.put(playlist, songsList);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        //if playList already exists.
        for(Playlist playlist: playlists){

            if(playlist.getTitle().equals(title)){

                return playlist;

            }
        }

        //getting user.
        boolean userExist = false;
        User creatorOfPlaylist = null;
        for(User user: users){

            if(user.getMobile().equals(mobile)){

                userExist = true;
                creatorOfPlaylist = user;
                break;

            }
        }

        //user not exist.
        if(!userExist){

            throw new Exception("User does not exist");

        }

        //create playlist.
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        //creator playlist mapping.
        creatorPlaylistMap.put(creatorOfPlaylist, playlist);

        //user playList mapping.
        userPlaylistMap.put(creatorOfPlaylist, new ArrayList<>());
        userPlaylistMap.get(creatorOfPlaylist).add(playlist);

        //adding current listener(creator)
        playlistListenerMap.put(playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(creatorOfPlaylist);

        //adding songs to playlist.
        List<Song> songsList = new ArrayList<>();
        for(Song song: songs){

            if(songTitles.contains(song.getTitle())){

                songsList.add(song);

            }
        }

        //creating playlist-> songs DB.
        playlistSongMap.put(playlist, songsList);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        boolean playlistExist = false;
        Playlist playlistNeeded = null;
        //finding the playlist.
        for(Playlist playlist: playlists){

            if(playlist.getTitle().equals(playlistTitle)){

                playlistExist = true;
                playlistNeeded = playlist;
                break;
            }

        }

        if(!playlistExist){
            throw new Exception("Playlist does not exist");
        }


        //finding user.
        boolean userExist = false;
        User userNeeded = null;
        for(User user: users){

            if(user.getMobile().equals(mobile)){

                userExist = true;
                userNeeded = user;
                break;

            }
        }

        if(!userExist){

            throw new Exception("User does not exist");

        }

        //if user already a listener
        if(playlistListenerMap.get(playlistNeeded).contains(userNeeded)){

            return playlistNeeded;

        }

        //if user is a creator.
        if(creatorPlaylistMap.containsKey(userNeeded)){

            if(creatorPlaylistMap.get(userNeeded).equals(playlistNeeded)){
                return playlistNeeded;
            }
        }

        //adding user.
        playlistListenerMap.get(playlistNeeded).add(userNeeded);

        return playlistNeeded;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {

        //getting user.
        boolean userExist = false;
        User userLiked = null;
        for(User user: users){

            if(user.getMobile().equals(mobile)){
                userLiked = user;
                userExist = true;
                break;
            }
        }

        if(!userExist){
            throw new Exception("User does not exist");
        }

        //getting song.
        boolean songExist = false;
        Song songLiked = null;
        for(Song song: songs){

            if(song.getTitle().equals(songTitle)){

                songExist = true;
                songLiked = song;
                break;

            }
        }

        if(!songExist){
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.get(songLiked).contains(userLiked)){

            return songLiked;
        }

        //updating song likes, artist likes
        songLikeMap.get(songLiked).add(userLiked);
        songLiked.setLikes(songLiked.getLikes() + 1);

        //get artist.
        //1. find album.
        //2. get artist.
        Album albumNeeded = null;
        for(Album album: albums){

            if(albumSongMap.get(album).contains(songLiked)){
                albumNeeded = album;
                break;
            }
        }

        Artist artistNeeded = null;
        for(Artist artist: artists){

            if(artistAlbumMap.get(artist).contains(albumNeeded)){

                artistNeeded = artist;
                break;

            }
        }

        artistNeeded.setLikes(artistNeeded.getLikes() + 1);

        return songLiked;

    }

    public String mostPopularArtist() {

        int max = Integer.MIN_VALUE;
        String name = "";
        for(Artist artist: artists){

            if(artist.getLikes() > max){

                name = artist.getName();
                max = artist.getLikes();

            }

        }
        return name;
    }

    public String mostPopularSong() {

        int max = Integer.MIN_VALUE;
        String name = "";
        for(Song song: songs){

            if(song.getLikes() > max){

                name = song.getTitle();
                max = song.getLikes();

            }

        }
        return name;
    }
}
