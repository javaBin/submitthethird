package no.java.submit

import javax.servlet.*
import javax.servlet.http.HttpServletResponse

class AddStrictSecurityHeaderFilter:Filter {
    override fun destroy() {
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        chain?.doFilter(request,response)
        if (response is HttpServletResponse) {
            response.addHeader("Strict-Transport-Security", "max-age=31536000")
        }
    }

    override fun init(filterConfig: FilterConfig?) {
    }

}
