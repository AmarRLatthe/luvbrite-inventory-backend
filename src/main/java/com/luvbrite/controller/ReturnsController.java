package com.luvbrite.controller;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luvbrite.commonresponse.CommonResponse;
import com.luvbrite.model.CancelOrderDTO;
import com.luvbrite.model.PaginatedReturns;
import com.luvbrite.model.UserDetails;
import com.luvbrite.service.IPacketService;
import com.luvbrite.service.IReturnsService;
import com.luvbrite.service.IUserService;
import com.luvbrite.service.Tracker;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/return/")
@Slf4j
public class ReturnsController {

    @Autowired
    private IReturnsService returnService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IPacketService iPacketService;

    @Autowired
    private Tracker tracker;



    @PostMapping("/addreturn")
    ResponseEntity<CommonResponse> addReturn(@RequestBody CancelOrderDTO cancelOrderDTO,
                                             Authentication authentication) {

        log.info("qwertyuiop ====>>>>>>>>>>> ");
        CommonResponse response = new CommonResponse();

        UserDetails userDetails = iUserService.getByUsername(authentication.getName());
        log.info("qwertyuiop ====>>>>>>>>>>> {}",cancelOrderDTO);
        // Validation
        if (Objects.isNull(cancelOrderDTO) || StringUtils.isBlank(cancelOrderDTO.getPacketCode())
                || StringUtils.isBlank(cancelOrderDTO.getReason())) {

        	
            response.setCode(400);
            response.setData(false);
            response.setStatus("FAILED");
            response.setMessage("Invalid Packet Code, reason");

            return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);

        }

        String packetCode = cancelOrderDTO.getPacketCode();
        String reason = cancelOrderDTO.getReason();
        try {
            if (userDetails != null) {

                int shopId = userDetails.getShopId();
                int operatorId = userDetails.getId();

                if (!iPacketService.checkIfValidBarcode(packetCode, shopId)) {

                    response.setCode(200);
                    response.setData(false);
                    response.setMessage(packetCode + "is not a valid barcode");
                    response.setStatus("SUCCESS");

                    return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
                }

                int returnstatus = returnService.addReturn(packetCode, reason, shopId, operatorId);

                if (returnstatus == 0) {

                    response.setCode(200);
                    response.setData(false);
                    response.setMessage("Packet Code " + packetCode + "already marked as returned ");
                    response.setStatus("ALREADY RETURNED");

                } else if (returnstatus == 1) {

                    response.setCode(200);
                    response.setData(true);
                    response.setMessage("Packet Code " + packetCode + " marked as returned successfully");
                    response.setStatus("RETURNED");

                } else {
                    response.setCode(200);
                    response.setData(true);
                    response.setMessage("Packet " + packetCode + " could not be updated as returned ");
                    response.setStatus("FAILED");
                }

            } else {

                response.setCode(401);
                response.setData("");
                response.setMessage("Accessing user does not exists ");
                response.setStatus("FAILURE");
            }
            return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {

            log.error("Exception occured while returning packet : " + packetCode, e);
            response.setCode(500);
            response.setData(false);
            response.setMessage("Exception occured while returning packet : " + packetCode);
            response.setStatus("FAILED");

            return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
        }

    }

    @GetMapping("/returnreason")
    ResponseEntity<CommonResponse> listReason(Authentication authentication) {

        CommonResponse response = new CommonResponse();

        UserDetails userDetails = iUserService.getByUsername(authentication.getName());

        if (userDetails != null) {
            int shopId = userDetails.getShopId();
            List<String> reasons = returnService.listReasons(shopId);

            response.setCode(200);
            response.setData(reasons);
            response.setMessage("List of reasons retrieved successfully !!");
            response.setStatus("SUCCESS");

        } else {
            response.setCode(200);

            response.setMessage("List of reasons could not be retrieved successfully !!");
            response.setStatus("FAILED");
        }

        return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
    }


    @GetMapping("/listreturns")
    ResponseEntity<CommonResponse> listReturns(@RequestParam(value = "cpage", required = true) Integer currentPage,
                                               Authentication authentication) throws Exception {

        currentPage = currentPage == null ? 0 : currentPage;

        CommonResponse response = new CommonResponse();

        UserDetails userDetails = iUserService.getByUsername(authentication.getName());

        if (userDetails != null) {
            int shopId = userDetails.getShopId();
            PaginatedReturns paginatedReturns = null;
            try {
                paginatedReturns = returnService.listReturns(currentPage, shopId);
                response.setCode(200);
                response.setData(paginatedReturns);
                response.setMessage("Fetched list of returns successfully");
                response.setStatus("SUCCESS");

            } catch (Exception ex) {
                log.error("Exception occured while fetching lsit of returns", ex);
                response.setCode(500);
                response.setMessage("Failed fetching list of returns");
                response.setStatus("FAILED");
            }
        } else {
            response.setCode(401);
            response.setData(false);
            response.setMessage("Authentication Failure , could not authorize");
            response.setStatus("FAILED");
        }

        return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
    }


    @DeleteMapping("/deletereturn/{returnId}")
    ResponseEntity<CommonResponse> removeReturn(@PathVariable(value = "returnId") Integer returnId,
                                                Authentication authentication) throws Exception {

        CommonResponse response = new CommonResponse();
        UserDetails userDetails = iUserService.getByUsername(authentication.getName());

        if (returnId == 0 ||userDetails.getId() == null ||userDetails.getId() == 0) {
            response.setCode(400);
            response.setStatus("FAILURE");
            response.setMessage("Invalid returnId/operatorId");

            return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);
        }

        if (userDetails != null) {

            int shopId = userDetails.getShopId();
            int operatorId = userDetails.getId();

            try {
                /***
                 * return -1: no valid packets found ;
                 *  return -2: maybe already deleted ;
                 *  return -3: packetId is 0 ;
                 *  return 0:  return could not be deleted successfully;
                 *  return 1:  returns deleted successfully;
                 * */
                int returnStatus = returnService.deleteReturn(returnId, shopId, operatorId);

                switch (returnStatus) {

                    case -3:
                        response.setCode(200);
                        response.setData(false);
                        response.setMessage("No valid packet found for returnId:" + returnId);
                        response.setStatus("FAILED");
                        break;

                    case -2:
                        response.setCode(404);
                        response.setData(false);
                        response.setMessage("Packet could not be marked as NOT returned");
                        response.setStatus("FAILED");
                        break;
                    case -1:
                        response.setCode(200);
                        response.setData(false);
                        response.setMessage("No valid packets found ");
                        response.setStatus("FAILED");
                        break;
                    case 0:
                        response.setCode(404);
                        response.setData(false);
                        response.setMessage("return could not be deleted successfully");
                        response.setStatus("FAILED");
                        break;

                    case 1:
                        response.setCode(200);
                        response.setData(true);
                        response.setMessage("returned packet deleted successfully");
                        response.setStatus("SUCCESS");
                        break;
                    default:
                        response.setCode(404);
                        response.setData(false);
                        response.setMessage("May be this packet is already marked as not returned");
                        response.setStatus("FAILED");
                        break;


                }

            } catch (Exception e) {
                log.error("Exception occured while deleting packet", e);
                response.setCode(500);
                response.setData(false);
                response.setMessage("Exception:Could not delete return for returnId " + returnId);
                response.setStatus("FAILED");
            }

        } else {
            log.error("Could not find user to authenticate");
            response.setStatus("FAILED");
            response.setMessage("Could not find user to authenticate");
            response.setCode(401);
        }


        return new ResponseEntity<CommonResponse>(response, HttpStatus.ACCEPTED);

    }


}
