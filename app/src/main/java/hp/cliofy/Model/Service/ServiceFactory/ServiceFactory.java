package hp.cliofy.Model.Service.ServiceFactory;

import hp.cliofy.Model.Service.AlbumService.AlbumService;
import hp.cliofy.Model.Service.AlbumService.IAlbumService;
import hp.cliofy.Model.Service.ArtistService.ArtistService;
import hp.cliofy.Model.Service.ArtistService.IArtistService;
import hp.cliofy.Model.Service.AuthenticationService.AuthenticationService;
import hp.cliofy.Model.Service.AuthenticationService.IAuthenticationService;
import hp.cliofy.Model.Service.PlayerService.IPlayerService;
import hp.cliofy.Model.Service.PlayerService.PlayerService;
import hp.cliofy.Model.Service.PlaylistService.IPlaylistService;
import hp.cliofy.Model.Service.PlaylistService.PlaylistService;
import hp.cliofy.Model.Service.TrackService.ITrackService;
import hp.cliofy.Model.Service.TrackService.TrackService;
import hp.cliofy.Model.Service.UserService.IUserService;
import hp.cliofy.Model.Service.UserService.UserService;

public class ServiceFactory implements IServiceFactory {
    @Override
    public IAlbumService createAlbumService() {
        return new AlbumService();
    }

    @Override
    public IArtistService createArtistService() {
        return new ArtistService();
    }

    @Override
    public IAuthenticationService createAuthenticationService() {
        return new AuthenticationService();
    }

    @Override
    public IPlayerService createPlayerService() {
        return new PlayerService();
    }

    @Override
    public IPlaylistService createPlaylistService() {
        return new PlaylistService();
    }

    @Override
    public ITrackService createTrackService() {
        return new TrackService();
    }

    @Override
    public IUserService createUserService() {
        return new UserService();
    }
}
