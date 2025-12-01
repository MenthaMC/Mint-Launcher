package dev.menthamc.mintlauncher.data

/**
 * 下载源列表。ORIGIN 代表不走代理。
 */
enum class ProxySource(val baseUrl: String) {
    ORIGIN(""),
    GHFAST("https://ghfast.top"),
    GH_PROXY("https://gh-proxy.com"),
    GHFILE("https://ghfile.geekertao.top"),
    GH_PROXY_NET("https://gh-proxy.net"),
    J1WIN("https://j.1win.ggff.net"),
    GHM("https://ghm.078465.xyz"),
    GITPROXY("https://gitproxy.127731.xyz"),
    JIASHU("https://jiashu.1win.eu.org");
}
